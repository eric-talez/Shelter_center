import pytest

from frontend import student_ui


class CaptureUI:
    def __init__(self):
        self.texts = []

    def label(self, text):
        self.texts.append(text)
        return self

    def classes(self, _):
        return self


@pytest.mark.parametrize('labels', [[], ['Custom supplied label', 'Second label']])
def test_completed_student_labels_are_returned_and_rendered(monkeypatch, labels):
    from frontend import ui_helpers
    animal = {'adoptable': True, 'fosterable': True}
    monkeypatch.setattr(student_ui, 'opportunity_labels', lambda supplied: labels if supplied is animal else None)
    capture = CaptureUI()
    monkeypatch.setattr(ui_helpers, 'ui', capture)
    actual = ui_helpers.get_opportunity_labels(animal)
    assert actual == labels
    ui_helpers.render_opportunity_badges(actual)
    assert capture.texts == labels


def test_only_dedicated_student_exception_means_unfinished(monkeypatch):
    from frontend import ui_helpers

    def unfinished(_):
        raise student_ui.ExerciseNotImplementedError('lesson-5')

    monkeypatch.setattr(student_ui, 'opportunity_labels', unfinished)
    assert ui_helpers.get_opportunity_labels({}) is None


@pytest.mark.parametrize('error', [KeyError('adoptable'), ValueError('bug'), RuntimeError('bug')])
def test_student_programming_errors_propagate(monkeypatch, error):
    from frontend import ui_helpers

    def broken(_):
        raise error

    monkeypatch.setattr(student_ui, 'opportunity_labels', broken)
    with pytest.raises(type(error)):
        ui_helpers.get_opportunity_labels({})


@pytest.mark.parametrize('value', [None, 'Mock adoption option', [True], ('label',)])
def test_wrong_student_output_is_programming_error(monkeypatch, value):
    from frontend import ui_helpers
    monkeypatch.setattr(student_ui, 'opportunity_labels', lambda _: value)
    with pytest.raises(TypeError):
        ui_helpers.get_opportunity_labels({})


@pytest.mark.parametrize(('summary', 'expected'), [
    ({'shelterCount': 2, 'speciesCounts': {'DOG': 3, 'CAT': 1}, 'unfinishedLessons': []},
     ['Shelters: 2', 'DOG: 3', 'CAT: 1']),
    ({'shelterCount': 0, 'speciesCounts': {'DOG': 0, 'CAT': 0}, 'unfinishedLessons': []},
     ['Shelters: 0', 'DOG: 0', 'CAT: 0']),
    ({'shelterCount': 1, 'speciesCounts': None, 'unfinishedLessons': ['lesson-7']},
     ['Shelters: 1', 'Summary learning exercises unfinished: lesson-7']),
    ({'shelterCount': None, 'speciesCounts': {'DOG': 1, 'CAT': 0}, 'unfinishedLessons': ['lesson-6']},
     ['DOG: 1', 'CAT: 0', 'Summary learning exercises unfinished: lesson-6']),
    ({'shelterCount': None, 'speciesCounts': None, 'unfinishedLessons': ['lesson-6', 'lesson-7']},
     ['Summary learning exercises unfinished: lesson-6, lesson-7']),
])
def test_summary_displays_available_values_and_missing_lessons(monkeypatch, summary, expected):
    from frontend import ui_helpers
    capture = CaptureUI()
    monkeypatch.setattr(ui_helpers, 'ui', capture)
    ui_helpers.render_summary(summary)
    assert capture.texts == expected


@pytest.mark.parametrize('page', ['catalog', 'detail'])
@pytest.mark.parametrize('labels', [None, [], ['Supplied opportunity']])
def test_pages_show_supplied_badges_or_one_notice_and_preserve_shelter_links(monkeypatch, page, labels):
    import asyncio
    import httpx
    from nicegui import Client, ui
    from frontend import app as shelter_app
    from frontend.api_client import ShelterApi
    from frontend.tests.test_api_client import ANIMAL, SHELTER, SUMMARY

    def student_labels(_):
        if labels is None:
            raise student_ui.ExerciseNotImplementedError('lesson-5')
        return labels

    monkeypatch.setattr(student_ui, 'opportunity_labels', student_labels)
    callbacks = []
    monkeypatch.setattr(ui, 'timer', lambda _, callback, **kwargs: callbacks.append(callback))

    def respond(request):
        if request.url.path == '/api/shelters':
            return httpx.Response(200, json={'items': [SHELTER]})
        if request.url.path == '/api/animals':
            return httpx.Response(200, json={'items': [ANIMAL, dict(ANIMAL, id='mock-2')],
                                             'total': 2, 'summary': SUMMARY})
        return httpx.Response(200, json=ANIMAL)

    async def run():
        async with httpx.AsyncClient(transport=httpx.MockTransport(respond), base_url='http://java.test') as client:
            monkeypatch.setattr(shelter_app, 'api', ShelterApi(client), raising=False)
            with Client(ui.page('/test')) as page_client:
                with ui.column() as root:
                    if page == 'catalog':
                        await shelter_app.catalog_page()
                    else:
                        await shelter_app.detail_page(ANIMAL['id'])
                await callbacks.pop()()
        elements = list(root.descendants())
        texts = [element.text for element in elements if hasattr(element, 'text')]
        notices = [text for text in texts if text.startswith('Learning exercise · lesson-5')]
        assert len(notices) == (1 if labels is None else 0)
        assert texts.count('Supplied opportunity') == (0 if not labels else (2 if page == 'catalog' else 1))
        assert any(element.props.get('href') == SHELTER['websiteUrl'] for element in elements)
        if page == 'catalog':
            assert 'Summary learning exercises unfinished: lesson-6, lesson-7' in texts
        page_client.delete()
    asyncio.run(run())
