import asyncio

import httpx

from frontend.api_client import ShelterApi
from frontend.app import build_search_params, refresh_catalog
from frontend.tests.test_api_client import ANIMAL, SHELTER, SUMMARY, ZERO_SUMMARY


def test_search_params_keep_zero_and_backend_values_without_shelter_filter():
    assert build_search_params('CAT', 0, 'A&B', 'FOSTER') == [
        ('species', 'CAT'), ('maxAgeMonths', '0'), ('q', 'A&B'), ('purpose', 'FOSTER'),
    ]
    assert build_search_params(None, '', '', None) == []


def test_older_request_completion_cannot_overwrite_newer_selection():
    async def run():
        first_started = asyncio.Event()
        release_first = asyncio.Event()

        async def respond(request):
            if request.url.path == '/api/shelters':
                return httpx.Response(200, json={'items': [SHELTER]})
            if request.url.params.get('species') == 'DOG':
                first_started.set()
                await release_first.wait()
                return httpx.Response(200, json={'items': [ANIMAL], 'total': 1, 'summary': SUMMARY})
            return httpx.Response(200, json={'items': [], 'total': 0, 'summary': ZERO_SUMMARY})

        state = {}
        async with httpx.AsyncClient(transport=httpx.MockTransport(respond), base_url='http://java.test') as client:
            api = ShelterApi(client)
            older = asyncio.create_task(refresh_catalog(api, [('species', 'DOG')], state, lambda: None))
            await first_started.wait()
            await refresh_catalog(api, [('species', 'CAT')], state, lambda: None)
            release_first.set()
            await older
        assert state['items'] == []
        assert state['total'] == 0
        assert state['summary'] == ZERO_SUMMARY
        assert state['error'] is None
        assert state['loading'] is False
    asyncio.run(run())


def test_failed_refresh_clears_previous_matches_as_soon_as_it_starts():
    async def run():
        async def respond(request):
            if request.url.path == '/api/shelters':
                return httpx.Response(200, json={'items': [SHELTER]})
            raise httpx.ConnectError('Java stopped', request=request)
        state = {'items': [ANIMAL], 'total': 1, 'summary': SUMMARY}
        snapshots = []
        async with httpx.AsyncClient(transport=httpx.MockTransport(respond), base_url='http://java.test') as client:
            await refresh_catalog(ShelterApi(client), [], state,
                                  lambda: snapshots.append(dict(state)))
        assert snapshots[0]['loading'] is True
        assert snapshots[0]['items'] == []
        assert snapshots[0]['total'] is None
        assert snapshots[0]['summary'] is None
        assert state['items'] == []
        assert state['total'] is None
        assert state['summary'] is None
        assert state['error'].code == 'BACKEND_UNAVAILABLE'
        assert state['loading'] is False
    asyncio.run(run())


def test_successful_refresh_clears_summary_while_loading():
    async def run():
        def respond(request):
            if request.url.path == '/api/shelters':
                return httpx.Response(200, json={'items': [SHELTER]})
            return httpx.Response(200, json={'items': [], 'total': 0, 'summary': ZERO_SUMMARY})
        state = {'items': [ANIMAL], 'total': 1, 'summary': SUMMARY}
        snapshots = []
        async with httpx.AsyncClient(transport=httpx.MockTransport(respond), base_url='http://java.test') as client:
            await refresh_catalog(ShelterApi(client), [], state,
                                  lambda: snapshots.append(dict(state)))
        assert snapshots[0]['summary'] is None
        assert snapshots[-1]['summary'] == ZERO_SUMMARY
    asyncio.run(run())
