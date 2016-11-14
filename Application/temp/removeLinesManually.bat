convert ^
  testEdgeDetector.png ^
  -strip ^
  -write mpr:ORG ^
  ( +clone ^
    -negate ^
    -morphology Erode rectangle:50x1 ^
    -mask mpr:ORG -morphology Dilate rectangle:50x1 ^
    +mask ^
  ) ^
  -compose Lighten -composite ^
  ( +clone ^
    -morphology HMT "1x4:1,0,0,1" ^
  ) ^
  -compose Lighten -composite ^
  ( +clone ^
    -morphology HMT "1x3:1,0,1" ^
  ) ^
  -compose Lighten -composite ^
  ( +clone ^
    -morphology HMT "3x1:1,0,1" ^
  ) ^
  -compose Lighten -composite ^
  manuallyRemoved.png