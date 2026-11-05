"""Optional Python exercise: keep the useful profile while this is unfinished."""
from nicegui import ui


def render_opportunities(animal: dict) -> None:
    # TODO (lesson-6): Use if statements and animal's flags to explain opportunities.
    with ui.column().classes('learning-note w-full'):
        ui.label('Learning exercise · lesson-6').classes('font-bold')
        ui.label('Opportunity labels are unfinished. In frontend/student_ui.py, use the '
                 'adoptable and fosterable values to add them. The profile above is still usable.')
        ui.label('For real adoption or foster opportunities, contact the shelter directly.')
