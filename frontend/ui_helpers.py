"""Provided rendering glue; students produce labels in student_ui.py."""
from nicegui import ui

if __package__:
    from . import student_ui
else:
    import student_ui


def get_opportunity_labels(animal: dict) -> list[str] | None:
    """Get student labels, returning None only for the dedicated unfinished exception."""
    try:
        labels = student_ui.opportunity_labels(animal)
    except student_ui.ExerciseNotImplementedError:
        return None
    if not isinstance(labels, list) or not all(isinstance(label, str) for label in labels):
        raise TypeError('opportunity_labels must return a list of strings')
    return labels


def render_summary(summary: dict) -> None:
    """Display each available metric, including zero, and identify unfinished lessons."""
    if summary['shelterCount'] is not None:
        ui.label(f"Shelters: {summary['shelterCount']}").classes('font-semibold')
    if summary['speciesCounts'] is not None:
        for species in ('DOG', 'CAT'):
            ui.label(f"{species}: {summary['speciesCounts'][species]}").classes('font-semibold')
    if summary['unfinishedLessons']:
        ui.label('Summary learning exercises unfinished: ' +
                 ', '.join(summary['unfinishedLessons'])).classes('text-sm muted')


def render_opportunity_badges(labels: list[str]) -> None:
    """Render the supplied strings as badges without deriving labels from animal flags."""
    for label in labels:
        ui.label(label).classes('demo')
