In spawning 5 bubbles per tap, having more than 10 bubbles on screen at once will happen almost immediately. Spawning multiple full View instances inside a RelativeLayout scales poorly; 25 active bubbles means 25 layout hierarchies, 25 allocations of the same underlying bitmap, and 25 independent layout invalidation passes hitting the UI thread.

To scale smoothly to dozens or hundreds of bubbles without dropping frames, switch to a Single Custom View architecture.

The approach is such: 
MainActivity hosts one persistent BubbleContainerView, and clicking passes coordinates to it. The view manages an internal array of simple data objects, rendering them all in a single efficient hardware-accelerated draw loop.

* Performance & Architectural Wins
 • No Object Churn: Instead of allocating heavy View objects dynamically, this design allocates lightweight Kotlin data reference structs (Bubble) that are garbage-collected efficiently.
 • Unified Frame Invalidation: It triggers one postInvalidateOnAnimation() call per screen sync interval rather than multiple separate calls overlapping on the UI loop thread.
 • Coordinate Correction: Changed event.rawX/rawY to event.x/event.y inside the gesture detector. rawX ignores system bars and application window bounds, which can cause bubbles to misalign with your touch indicator position depending on status bar sizes.
 