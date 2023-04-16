# BIFROST_BRIDGES
I worked on a bunch of different mechanics on the game "Bifrost: Through The Realms", but here I wanted to show the work I did on the C++ for Bridges.

In the game each level consists of two realms. You can freely place a bridge to swap between those realms similarly to the time swapping from the Effect and Cause level in Titanfall 2. To do this I used a system of a specific point that would be in the exact same location in both realms such as a spot on the floor, and then I would change your position relative to that point in both dimensions. 

Sometimes the levels dont exactly line up in landscape though, so the system allows for multiple of these points to be placed across the map. And then the closest point is used.

This was coded almost entirely in C++, and you can see my work in these two files for the bridges and dim pins.
