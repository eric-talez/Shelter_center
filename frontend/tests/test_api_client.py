import asyncio

import httpx
import pytest

from frontend.api_client import ApiError, ShelterApi


ANIMAL = {
    'id': 'mock-1', 'name': 'Luna', 'species': 'DOG', 'breed': 'Terrier mix',
    'ageMonths': 0, 'description': 'Fictional profile', 'imageUrl': '/assets/dog.svg',
    'shelterId': 'sfacc', 'adoptable': True, 'fosterable': False, 'demo': True,
}
SHELTER = {
    'id': 'sfacc', 'name': 'San Francisco Animal Care & Control', 'city': 'San Francisco',
    'websiteUrl': 'https://www.sfanimalcare.org/adoptable-animals/',
    'phone': '+1-415-554-6364', 'sourceUrl': 'https://www.sfanimalcare.org/contact/',
}


def run_request(handler, method='animals', argument=None):
    async def run():
        async with httpx.AsyncClient(transport=httpx.MockTransport(handler), base_url='http://java.test') as client:
            api = ShelterApi(client)
            call = getattr(api, method)
            return await call(argument) if argument is not None else await call()
    return asyncio.run(run())


@pytest.mark.parametrize(('method', 'argument', 'path', 'payload', 'expected'), [
    ('animals', None, '/api/animals', {'items': [ANIMAL], 'total': 1}, {'items': [ANIMAL], 'total': 1}),
    ('shelters', None, '/api/shelters', {'items': [SHELTER]}, [SHELTER]),
    ('animal', 'mock-1', '/api/animals/mock-1', ANIMAL, ANIMAL),
])
def test_success_shapes(method, argument, path, payload, expected):
    def respond(request):
        assert request.url.path == path
        assert request.extensions['timeout']['read'] == 5.0
        return httpx.Response(200, json=payload)
    assert run_request(respond, method, argument) == expected


def test_repeated_shelters_and_search_are_url_encoded_without_losing_zero():
    def respond(request):
        assert request.url.params.get_list('shelterId') == ['sf acc', 'hssv']
        assert request.url.params['q'] == 'A&B + cat'
        assert request.url.params['maxAgeMonths'] == '0'
        assert b'shelterId=sf+acc&shelterId=hssv' in request.url.query
        return httpx.Response(200, json={'items': [], 'total': 0})
    assert run_request(respond, argument=[('shelterId', 'sf acc'), ('shelterId', 'hssv'),
                                        ('q', 'A&B + cat'), ('maxAgeMonths', '0')]) == {'items': [], 'total': 0}


@pytest.mark.parametrize(('status', 'code', 'lesson'), [
    (501, 'EXERCISE_NOT_IMPLEMENTED', 'lesson-1'),
    (404, 'NOT_FOUND', None), (500, 'INTERNAL_ERROR', None), (400, 'INVALID_REQUEST', None),
])
def test_declared_http_errors_preserve_code_message_and_lesson(status, code, lesson):
    with pytest.raises(ApiError) as caught:
        run_request(lambda _: httpx.Response(status, json={'code': code, 'message': 'Java message', 'lessonId': lesson}))
    assert (caught.value.code, caught.value.message, caught.value.lesson_id) == (code, 'Java message', lesson)


@pytest.mark.parametrize(('exception', 'code'), [
    (httpx.ConnectError, 'BACKEND_UNAVAILABLE'), (httpx.ReadTimeout, 'BACKEND_TIMEOUT'),
])
def test_connection_and_timeout_remain_distinct(exception, code):
    def fail(request):
        raise exception('Cannot connect', request=request)
    with pytest.raises(ApiError) as caught:
        run_request(fail)
    assert caught.value.code == code


@pytest.mark.parametrize(('method', 'argument', 'payload'), [
    ('animals', None, []), ('animals', None, {'items': [], 'total': '0'}),
    ('animals', None, {'items': [{}], 'total': 1}),
    ('animals', None, {'items': [], 'total': -1}),
    ('animals', None, {'items': [dict(ANIMAL, ageMonths=True)], 'total': 1}),
    ('shelters', None, {'items': [{}]}), ('animal', 'mock-1', {'items': [ANIMAL]}),
])
def test_malformed_success_is_invalid_response(method, argument, payload):
    with pytest.raises(ApiError) as caught:
        run_request(lambda _: httpx.Response(200, json=payload), method, argument)
    assert caught.value.code == 'INVALID_RESPONSE'


def test_non_json_success_is_invalid_response():
    with pytest.raises(ApiError) as caught:
        run_request(lambda _: httpx.Response(200, text='<html>Proxy page</html>'))
    assert caught.value.code == 'INVALID_RESPONSE'


def test_non_json_server_error_does_not_become_empty_results():
    with pytest.raises(ApiError) as caught:
        run_request(lambda _: httpx.Response(500, text='Server failed'))
    assert caught.value.code == 'SERVER_ERROR'
