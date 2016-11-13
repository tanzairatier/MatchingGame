# MatchingGame
Matching Memory Game in Java


Press G or J for autosolver mode!  G toggles a brute force mode, selecting random available pairs.  This is essentially a worst case of O(infinity), but the average case is much more interesting.

J toggles a smarter mode, selecting a random card and locking it in while searching systematically for its match.  J is faster than G, since it will gaurantee a solution in less than O(infinity).

Sometimes a bug will occur during solver mode, causing the application to freeze.  A restart is necessary in that case.
