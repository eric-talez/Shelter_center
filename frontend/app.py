"""Run with `python frontend/app.py`; Java owns all catalog search rules."""
import os
from pathlib import Path

import httpx
from nicegui import app, ui

if __package__:
    from .api_client import ApiError, ShelterApi
    from .ui_helpers import get_opportunity_labels, render_opportunity_badges, render_summary
else:
    from api_client import ApiError, ShelterApi
    from ui_helpers import get_opportunity_labels, render_opportunity_badges, render_summary


API_URL = os.environ.get('SHELTER_API_URL', 'http://127.0.0.1:8080')
NOTICE = ('Learning demo — all animal profiles are fictional. Shelter names and links '
          'are real; this project is not affiliated with them.')
ASSETS = Path(__file__).parent / 'assets'
app.add_static_files('/assets', ASSETS)


@app.on_startup
async def open_api():
    global http_client, api
    http_client = httpx.AsyncClient(base_url=API_URL, timeout=5.0)
    api = ShelterApi(http_client)


@app.on_shutdown
async def close_api():
    await http_client.aclose()


def build_search_params(species, max_age, query, purpose):
    params = []
    if species:
        params.append(('species', species))
    if max_age is not None and str(max_age) != '':
        params.append(('maxAgeMonths', str(max_age)))
    if query:
        params.append(('q', query))
    if purpose:
        params.append(('purpose', purpose))
    return params


async def refresh_catalog(api, params, state, on_change):
    # Each page owns this dictionary; only its latest request may change the view.
    generation = state.get('generation', 0) + 1
    state.update(generation=generation, loading=True, items=[], total=None, summary=None, error=None)
    on_change()
    try:
        shelters = await api.shelters()
        result = await api.animals(params)
    except ApiError as error:
        if state['generation'] == generation:
            state.update(error=error, summary=None, loading=False)
            on_change()
    else:
        if state['generation'] == generation:
            state.update(shelters=shelters, items=result['items'], total=result['total'],
                         summary=result['summary'], loading=False)
            on_change()


ui.add_css('''
:root { --forest: #264f3b; --cream: #faf7ef; --ink: #233c31; }
body { background: var(--cream); color: var(--ink); font-family: system-ui, sans-serif; }
.nicegui-content { padding: 0; }
.page { width: min(1160px, 100%); margin: auto; padding: 32px 24px 60px; gap: 24px; }
.eyebrow { font-size: .78rem; text-transform: uppercase; letter-spacing: .16em; font-weight: 750; }
h1 { font-size: clamp(2.1rem, 6vw, 3.8rem); line-height: 1.08; margin: 8px 0 12px; font-weight: 750; }
h2 { font-size: 1.5rem; line-height: 1.25; margin: 0; font-weight: 700; }
.muted { color: #52665b; }
.notice, .learning-note { background: #eeeede; border: 1px solid #cad3bd; border-radius: 12px; padding: 16px; }
.filters { background: white; border: 1px solid #deded0; border-radius: 16px; padding: 22px; }
.filter-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); width: 100%; gap: 16px; }
.animal-grid { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 22px; width: 100%; }
.animal-card { width: 100%; border: 1px solid #dce0d1; border-radius: 16px; padding: 0; overflow: hidden; box-shadow: none; gap: 0; }
.animal-image { width: 100%; height: 195px; background: #edf0e3; }
.card-body { padding: 18px; gap: 8px; width: 100%; }
.demo { background: #e6ecdc; color: #31513a; padding: 3px 9px; border-radius: 20px; font-size: .75rem; font-weight: 700; }
a { color: #275c40; text-underline-offset: 3px; }
a:focus-visible, button:focus-visible { outline: 3px solid #a36d19; outline-offset: 4px; }
.error-box { background: #fff5e6; border: 1px solid #b98c49; border-radius: 12px; padding: 20px; }
.detail { background: white; border: 1px solid #deded0; border-radius: 16px; padding: 24px; }
@media (max-width: 850px) { .animal-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } .filter-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); } }
@media (max-width: 550px) { .page { padding: 24px 16px 40px; } .animal-grid, .filter-grid { grid-template-columns: minmax(0, 1fr); } .filters { padding: 16px; } }
''', shared=True)
app.colors(primary='#264f3b', secondary='#62735a')


def page_header():
    ui.label('Explore · Learn · Connect').classes('eyebrow')
    ui.html('Bay Area Shelter Hub', tag='h1').classes('text-4xl sm:text-5xl font-bold leading-tight')
    ui.label('Discover local shelters through fictional animal profiles, then connect '
             'with a real Bay Area shelter.').classes('text-lg muted')
    ui.label(NOTICE).classes('notice w-full')


def render_error(error, *, detail=False):
    with ui.column().classes('error-box w-full').props('role=alert'):
        if error.code == 'EXERCISE_NOT_IMPLEMENTED':
            ui.label(f'Learning exercise · {error.lesson_id or "unfinished"}').classes('font-bold text-lg')
            ui.label('This feature is waiting for your Java exercise. Implement the matching TODO in:')
            ui.label('backend/src/main/java/org/royshelter/AnimalExercises.java').classes('break-all font-mono text-sm')
            ui.label('Use Back to catalog to continue browsing shelters.' if detail else
                     'Use Reset to return to the whole catalog.').classes('muted')
        elif error.code in ('BACKEND_UNAVAILABLE', 'BACKEND_TIMEOUT'):
            ui.label('Java is unavailable' if error.code == 'BACKEND_UNAVAILABLE' else 'Java took too long').classes('font-bold text-lg')
            ui.label('Start the Java server on port 8080, then select Retry. Keep this page open.')
            ui.label('From the project folder, with JDK 21 configured:').classes('text-sm')
            ui.label('backend/mvnw -f backend/pom.xml spring-boot:run').classes('break-all font-mono text-sm')
        elif error.code == 'NOT_FOUND':
            ui.label('Animal profile not found.').classes('font-bold text-lg')
            ui.label('This profile may have been removed. Return to the catalog to choose another.')
        elif error.code == 'INVALID_REQUEST':
            ui.label('Please correct the search input.').classes('font-bold text-lg')
            ui.label('Use a whole number of months, 0 or greater, and the listed filter choices.')
        else:
            ui.label('We could not load this information.').classes('font-bold text-lg')
            ui.label('Please retry. If this continues, check the Java server output.')
        ui.label(f'{error.code}: {error.message}').classes('text-sm break-words')


def animal_image(animal):
    image = ui.image(animal['imageUrl'] or '/assets/placeholder.svg').classes('animal-image').props('fit=contain')
    image.props['alt'] = f"Illustration for {animal['name']}, a fictional {animal['species'].lower()}"
    image.on('error', lambda: image.set_source('/assets/placeholder.svg'))


def shelter_links(shelter):
    ui.link('Visit shelter website', shelter['websiteUrl'], new_tab=True).props('rel=noopener')
    ui.label('Real shelter website · applications and current availability are handled by the shelter.').classes('text-xs muted')


def opportunity_notice():
    ui.label('Learning exercise · lesson-5: opportunity labels are unfinished. '
             'Complete opportunity_labels in frontend/student_ui.py. '
             'For real opportunities, contact the shelter directly.').classes('learning-note w-full')


def animal_card(animal, shelters, labels):
    with ui.card().classes('animal-card'):
        animal_image(animal)
        with ui.column().classes('card-body'):
            with ui.row().classes('w-full items-center justify-between'):
                shelter = shelters.get(animal['shelterId'])
                with ui.element('h2').classes('text-xl font-bold'):
                    if shelter:
                        ui.link(animal['name'], shelter['websiteUrl'], new_tab=True).props('rel=noopener')
                    else:
                        ui.label(animal['name'])
                ui.label('Demo').classes('demo')
            ui.label(f"{animal['species'].title()} · {animal['ageMonths']} months · {animal['breed']}").classes('muted')
            if labels is not None:
                render_opportunity_badges(labels)
            shelter = shelters.get(animal['shelterId'])
            if shelter:
                ui.label(shelter['name']).classes('font-semibold')
                ui.label(shelter['city']).classes('text-sm muted')
            if shelter:
                shelter_links(shelter)


@ui.page('/')
async def catalog_page():
    state = {}
    with ui.column().classes('page'):
        page_header()
        with ui.column().classes('filters w-full'):
            ui.html('Find a companion, practice Java', tag='h2').classes('text-2xl font-bold')
            ui.label('Filters are learning exercises (lessons 1, 2 and 4). An unfinished '
                     'exercise will show a helpful note; the whole catalog works now.').classes('muted')
            with ui.element('div').classes('filter-grid'):
                species = ui.select({'DOG': 'Dog', 'CAT': 'Cat'}, label='Species · lesson-1', clearable=True).classes('w-full')
                max_age = ui.input('Maximum age in months · lesson-2').props('type=number min=0 step=1').classes('w-full')
                query = ui.input('Search name or breed · lesson-4').classes('w-full')
                purpose = ui.select({'ADOPT': 'Adoption', 'FOSTER': 'Foster'}, label='Purpose · lesson-4', clearable=True).classes('w-full')
            with ui.row():
                ui.button('Search', on_click=lambda: search()).props('no-caps')
                ui.button('Reset', on_click=lambda: reset()).props('outline no-caps')
        results = ui.column().classes('w-full gap-5').props('aria-live=polite aria-atomic=true')

    def show_results():
        results.clear()
        with results:
            if state['loading']:
                with ui.row().classes('items-center'):
                    ui.spinner(size='sm')
                    ui.label('Loading animal profiles…')
            elif state['error']:
                render_error(state['error'])
                ui.button('Retry', on_click=lambda: search()).props('outline no-caps')
            else:
                ui.label(f"{state['total']} animal profiles").classes('text-2xl font-bold').props('role=heading aria-level=2')
                ui.label('Fictional profiles · real local shelters').classes('muted')
                with ui.row().classes('w-full items-center gap-3'):
                    render_summary(state['summary'])
                opportunities = [(animal, get_opportunity_labels(animal)) for animal in state['items']]
                if any(labels is None for _, labels in opportunities):
                    opportunity_notice()
                if not state['items']:
                    ui.label('No animals match these filters.')
                shelters = {s['id']: s for s in state['shelters']}
                with ui.element('div').classes('animal-grid'):
                    for animal, labels in opportunities:
                        animal_card(animal, shelters, labels)

    async def search():
        params = build_search_params(species.value, max_age.value, query.value, purpose.value)
        await refresh_catalog(api, params, state, show_results)

    async def reset():
        species.set_value(None)
        max_age.set_value('')
        query.set_value('')
        purpose.set_value(None)
        await search()

    ui.timer(0.1, search, once=True)


@ui.page('/animals/{animal_id}')
async def detail_page(animal_id: str):
    with ui.column().classes('page'):
        page_header()
        ui.link('← Back to catalog', '/')
        content = ui.column().classes('w-full')

    async def load_detail():
        content.clear()
        with content:
            ui.label('Loading fictional profile…')
        try:
            animal = await api.animal(animal_id)
            shelters = await api.shelters()
        except ApiError as error:
            content.clear()
            with content:
                render_error(error, detail=True)
                ui.button('Retry', on_click=load_detail).props('outline no-caps')
        else:
            content.clear()
            with content, ui.column().classes('detail w-full'):
                ui.label('Fictional animal profile — this animal is not available for adoption.').classes('notice w-full')
                animal_image(animal)
                ui.label(animal['name']).classes('text-xl font-bold').props('role=heading aria-level=2')
                ui.label(f"{animal['species'].title()} · {animal['ageMonths']} months · {animal['breed']}")
                ui.label(animal['description'])
                for shelter in shelters:
                    if shelter['id'] == animal['shelterId']:
                        ui.label(shelter['name']).classes('font-semibold')
                        ui.label(shelter['city'])
                        shelter_links(shelter)
                        ui.link('Call shelter: ' + shelter['phone'], 'tel:' + shelter['phone'])
                labels = get_opportunity_labels(animal)
                if labels is None:
                    opportunity_notice()
                else:
                    render_opportunity_badges(labels)
    ui.timer(0.1, load_detail, once=True)


if __name__ == '__main__':
    ui.run(host='127.0.0.1', port=int(os.environ.get('FRONTEND_PORT', '8081')),
           title='Bay Area Shelter Hub', reload=False, show=False)
