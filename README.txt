JFreeChart 1.0.19
-----------------
31 July 2014

This maintenance release corrects some issues in the new JavaFX support that
was added in the 1.0.18 release, as well as some general issues.

Bug Fixes:

- fixed clipping issues for combined plots in JavaFX;
- fix a memory leak in the new JavaFX ChartCanvas class;
- CombinedDomainXYPlot and CombinedRangeXYPlot now take into account the
  pannable flags in the subplots;
- FastScatterPlot panning direction is corrected;
- added rendering hints to sharpen gridlines and borders in most output formats;
- JFreeSVG updated to version 2.0.

In this release, we have also provided a preview of JSFreeChart, a free 2D
chart library written in JavaScript that is conceptually similar to JFreeChart
but runs directly in browsers.
