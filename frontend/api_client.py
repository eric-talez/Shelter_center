"""The Python UI sends search rules to Java; it does not implement them."""
from urllib.parse import quote

import httpx


class ApiError(Exception):
    def __init__(self, code: str, message: str, lesson_id: str | None = None):
        super().__init__(message)
        self.code = code
        self.message = message
        self.lesson_id = lesson_id


def _valid_animal(value):
    if not isinstance(value, dict):
        return False
    strings = ('id', 'name', 'species', 'breed', 'description', 'imageUrl', 'shelterId')
    return (all(isinstance(value.get(key), str) for key in strings)
            and type(value.get('ageMonths')) is int and value['ageMonths'] >= 0
            and all(type(value.get(key)) is bool for key in ('adoptable', 'fosterable', 'demo')))


def _valid_shelter(value):
    return isinstance(value, dict) and all(isinstance(value.get(key), str) for key in
                                          ('id', 'name', 'city', 'websiteUrl', 'phone', 'sourceUrl'))


def _valid_summary(value):
    if not isinstance(value, dict) or not all(key in value for key in
                                            ('shelterCount', 'speciesCounts', 'unfinishedLessons')):
        return False
    shelter_count = value['shelterCount']
    species_counts = value['speciesCounts']
    if shelter_count is not None and (type(shelter_count) is not int or shelter_count < 0):
        return False
    if species_counts is not None and (
            not isinstance(species_counts, dict)
            or not all(type(species_counts.get(species)) is int and species_counts[species] >= 0
                       for species in ('DOG', 'CAT'))):
        return False
    expected_lessons = []
    if shelter_count is None:
        expected_lessons.append('lesson-6')
    if species_counts is None:
        expected_lessons.append('lesson-7')
    return value['unfinishedLessons'] == expected_lessons


class ShelterApi:
    def __init__(self, client: httpx.AsyncClient):
        self.client = client

    async def _get(self, path, params=None):
        try:
            response = await self.client.get(path, params=params, timeout=5.0)
        except httpx.TimeoutException as error:
            raise ApiError('BACKEND_TIMEOUT', 'Java took too long to respond. Please retry.') from error
        except httpx.RequestError as error:
            raise ApiError('BACKEND_UNAVAILABLE', 'Cannot connect to the Java server.') from error
        try:
            payload = response.json()
        except ValueError:
            payload = None
        if not response.is_success:
            if (isinstance(payload, dict) and isinstance(payload.get('code'), str)
                    and isinstance(payload.get('message'), str)
                    and (payload.get('lessonId') is None or isinstance(payload['lessonId'], str))):
                raise ApiError(payload['code'], payload['message'], payload.get('lessonId'))
            code = 'SERVER_ERROR' if response.status_code >= 500 else 'HTTP_ERROR'
            raise ApiError(code, f'Java returned HTTP {response.status_code}. Please retry.')
        return payload

    async def animals(self, params: list[tuple[str, str]] | None = None) -> dict:
        value = await self._get('/api/animals', params)
        if (not isinstance(value, dict) or not isinstance(value.get('items'), list)
                or not all(_valid_animal(item) for item in value['items'])
                or type(value.get('total')) is not int or value['total'] < 0
                or not _valid_summary(value.get('summary'))):
            raise ApiError('INVALID_RESPONSE', 'Java returned an invalid animal list.')
        return value

    async def shelters(self) -> list[dict]:
        value = await self._get('/api/shelters')
        if (not isinstance(value, dict) or not isinstance(value.get('items'), list)
                or not all(_valid_shelter(item) for item in value['items'])):
            raise ApiError('INVALID_RESPONSE', 'Java returned an invalid shelter list.')
        return value['items']

    async def animal(self, animal_id: str) -> dict:
        value = await self._get('/api/animals/' + quote(animal_id, safe=''))
        if not _valid_animal(value):
            raise ApiError('INVALID_RESPONSE', 'Java returned an invalid animal profile.')
        return value
