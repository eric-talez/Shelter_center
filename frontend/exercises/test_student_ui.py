"""Opt-in lesson-5 acceptance checks; no server or browser is needed."""
import pytest

from frontend.student_ui import opportunity_labels


@pytest.mark.parametrize(('animal', 'expected'), [
    ({'adoptable': True, 'fosterable': False}, ['Mock adoption option']),
    ({'adoptable': False, 'fosterable': True}, ['Mock foster option']),
    ({'adoptable': True, 'fosterable': True}, ['Mock adoption option', 'Mock foster option']),
    ({'adoptable': False, 'fosterable': False}, []),
])
def test_opportunity_labels(animal, expected):
    original = dict(animal)
    labels = opportunity_labels(animal)
    assert isinstance(labels, list)
    assert labels == expected
    assert animal == original
