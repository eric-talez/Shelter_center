"""Pure Python lesson: build labels; the provided UI helpers display them."""


class ExerciseNotImplementedError(Exception):
    """Identify an unfinished student exercise without hiding programming errors."""


def opportunity_labels(animal: dict) -> list[str]:
    """Return opportunity strings for one animal dictionary without changing it.

    Read the independent boolean values at animal['adoptable'] and animal['fosterable'].
    Build a new list of strings: 'Mock adoption option' before 'Mock foster option'.
    Both True means both labels, in that order; both False means an empty list.
    For example, True/False means ['Mock adoption option']; False/True means
    ['Mock foster option']. These are fictional opportunities, not real applications.
    The provided get_opportunity_labels helper recognizes only the dedicated exception;
    render_opportunity_badges displays your strings without deciding which to create.
    """
    # Step 1: Prepare a new local list for the result strings.
    # Step 2: Read both boolean values from the input dictionary.
    # Step 3: Check each independent flag and add its exact label when True.
    # Step 4: Return the list; remove the final lesson exception when complete.
    raise ExerciseNotImplementedError('lesson-5')
