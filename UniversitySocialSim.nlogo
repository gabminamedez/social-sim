globals [
  heading-range
  forward-movement-range
  vision-nonverbal
  angle-nonverbal
  vision-exchange
  angle-exchange
  vision-cooperative
  angle-cooperative

  chance-interact

  num-student-student
  num-student-prof
  num-student-staff
  num-prof-prof
  num-prof-staff
  num-staff-staff
  num-nonverbal
  num-cooperative
  num-exchange
  student-ctr
  prof-ctr
  staff-ctr
  one-way?
]

patches-own [
  is-classroom
  is-bathroom
  is-staffroom
  is-hallway
  is-wall
  location
]

turtles-own [
  gender
  age
  interaction-time
  class1
  class2
  class3
]

breed [ students student ]
breed [ professors professor ]
breed [ staffs staff ]

to setup
  clear-all
  reset-ticks

  set-globals
  set-patches
  set-agents
  set-destinations
end

to set-globals
  set heading-range 45
  set forward-movement-range 2
  set vision-nonverbal 3
  set angle-nonverbal 45
  set vision-cooperative 1
  set angle-cooperative 30
  set vision-exchange 2
  set angle-exchange 30

  set num-student-student 0
  set num-student-prof 0
  set num-student-staff 0
  set num-prof-prof 0
  set num-prof-staff 0
  set num-staff-staff 0
  set num-nonverbal 0
  set num-cooperative 0
  set num-exchange 0
  ifelse entrance-mode = "one-way"[set one-way? true][set one-way? false]
end

to set-patches
  ; For hallways and walls
  ask patches [
    ifelse member? pxcor (list 11 12 13 14 15 16 17 18 19 20) [
      set pcolor white
      set is-classroom 0 set is-bathroom 0 set is-staffroom 0 set is-hallway 1 set is-wall 0
      set location "hallway"
    ] [
      set pcolor gray
      set is-classroom 0 set is-bathroom 0 set is-staffroom 0 set is-hallway 0 set is-wall 1
      set location "wall"
    ]
  ]
  ; For bottom right classroom
  ask (patch-set patch 22 7 patch 23 7 patch 24 7 patch 25 7 patch 26 7 patch 27 7 patch-set patch 22 8 patch 23 8 patch 24 8 patch 25 8 patch 26 8 patch 27 8 patch-set patch 21 9 patch 22 9 patch 23 9 patch 24 9 patch 25 9 patch 26 9 patch 27 9
       patch-set patch 21 10 patch 22 10 patch 23 10 patch 24 10 patch 25 10 patch 26 10 patch 27 10 patch-set patch 22 11 patch 23 11 patch 24 11 patch 25 11 patch 26 11 patch 27 11 patch-set patch 22 12 patch 23 12 patch 24 12 patch 25 12 patch 26 12 patch 27 12) [
    set pcolor yellow
    set is-classroom 1 set is-bathroom 0 set is-staffroom 0 set is-hallway 0 set is-wall 0
    set location "br"
  ]

  ; For middle right classroom
  ask (patch-set patch 22 14 patch 23 14 patch 24 14 patch 25 14 patch 26 14 patch 27 14 patch-set patch 22 15 patch 23 15 patch 24 15 patch 25 15 patch 26 15 patch 27 15 patch-set patch 21 16 patch 22 16 patch 23 16 patch 24 16 patch 25 16 patch 26 16 patch 27 16
       patch-set patch 21 17 patch 22 17 patch 23 17 patch 24 17 patch 25 17 patch 26 17 patch 27 17 patch-set patch 22 18 patch 23 18 patch 24 18 patch 25 18 patch 26 18 patch 27 18 patch-set patch 22 19 patch 23 19 patch 24 19 patch 25 19 patch 26 19 patch 27 19 ) [
    set pcolor yellow
    set is-classroom 1 set is-bathroom 0 set is-staffroom 0 set is-hallway 0 set is-wall 0
    set location "mr"
  ]

  ; For top right classroom
  ask (patch-set patch 22 21 patch 23 21 patch 24 21 patch 25 21 patch 26 21 patch 27 21 patch-set patch 22 22 patch 23 22 patch 24 22 patch 25 22 patch 26 22 patch 27 22 patch-set patch 21 23 patch 22 23 patch 23 23 patch 24 23 patch 25 23 patch 26 23 patch 27 23
       patch-set patch 21 24 patch 22 24 patch 23 24 patch 24 24 patch 25 24 patch 26 24 patch 27 24 patch-set patch 22 25 patch 23 25 patch 24 25 patch 25 25 patch 26 25 patch 27 25 patch-set patch 22 26 patch 23 26 patch 24 26 patch 25 26 patch 26 26 patch 27 26) [
    set pcolor yellow
    set is-classroom 1 set is-bathroom 0 set is-staffroom 0 set is-hallway 0 set is-wall 0
    set location "tr"
  ]

  ; For bottom left classroom
  ask (patch-set patch 4 7 patch 5 7 patch 6 7 patch 7 7 patch 8 7 patch 9 7 patch-set patch 4 8 patch 5 8 patch 6 8 patch 7 8 patch 8 8 patch 9 8 patch-set patch 4 9 patch 5 9 patch 6 9 patch 7 9 patch 8 9 patch 9 9 patch 10 9
       patch-set patch 4 10 patch 5 10 patch 6 10 patch 7 10 patch 8 10 patch 9 10 patch 10 10 patch-set patch 4 11 patch 5 11 patch 6 11 patch 7 11 patch 8 11 patch 9 11 patch-set patch 4 12 patch 5 12 patch 6 12 patch 7 12 patch 8 12 patch 9 12) [
    set pcolor yellow
    set is-classroom 1 set is-bathroom 0 set is-staffroom 0 set is-hallway 0 set is-wall 0
    set location "bl"
  ]

  ; For middle left classroom
  ask (patch-set patch 4 14 patch 5 14 patch 6 14 patch 7 14 patch 8 14 patch 9 14 patch-set patch 4 15 patch 5 15 patch 6 15 patch 7 15 patch 8 15 patch 9 15 patch-set patch 4 16 patch 5 16 patch 6 16 patch 7 16 patch 8 16 patch 9 16 patch 10 16
       patch-set patch 4 17 patch 5 17 patch 6 17 patch 7 17 patch 8 17 patch 9 17 patch 10 17 patch-set patch 4 18 patch 5 18 patch 6 18 patch 7 18 patch 8 18 patch 9 18 patch-set patch 4 19 patch 5 19 patch 6 19 patch 7 19 patch 8 19 patch 9 19) [
    set pcolor yellow
    set is-classroom 1 set is-bathroom 0 set is-staffroom 0 set is-hallway 0 set is-wall 0
    set location "ml"
  ]

  ; For top left classroom
  ask (patch-set patch 4 21 patch 5 21 patch 6 21 patch 7 21 patch 8 21 patch 9 21 patch-set patch 4 22 patch 5 22 patch 6 22 patch 7 22 patch 8 22 patch 9 22 patch-set  patch 4 23 patch 5 23 patch 6 23 patch 7 23 patch 8 23 patch 9 23 patch 10 23
    patch-set  patch 4 24 patch 5 24 patch 6 24 patch 7 24 patch 8 24 patch 9 24 patch 10 24 patch-set  patch 4 25 patch 5 25 patch 6 25 patch 7 25 patch 8 25 patch 9 25 patch-set  patch 4 26 patch 5 26 patch 6 26 patch 7 26 patch 8 26 patch 9 26) [
    set pcolor yellow
    set is-classroom 1 set is-bathroom 0 set is-staffroom 0 set is-hallway 0 set is-wall 0
    set location "tl"
  ]

  ; For bathroom
  ask (patch-set patch 5 2 patch 6 2 patch 7 2 patch 8 2 patch 9 2 patch 10 2 patch-set patch 5 3 patch 6 3 patch 7 3 patch 8 3 patch 9 3 patch 10 3
       patch-set patch 5 4 patch 6 4 patch 7 4 patch 8 4 patch 9 4 patch 10 4 patch-set patch 5 5 patch 6 5 patch 7 5 patch 8 5 patch 9 5 patch 10 5) [
    set pcolor blue
    set is-classroom 0 set is-bathroom 1 set is-staffroom 0 set is-hallway 0 set is-wall 0
    set location "bathroom"
  ]

  ; For staff room
  ask (patch-set patch 21 2 patch 22 2 patch 23 2 patch 24 2 patch 25 2 patch 26 2 patch-set patch 21 3 patch 22 3 patch 23 3 patch 24 3 patch 25 3 patch 26 3
       patch-set patch 21 4 patch 22 4 patch 23 4 patch 24 4 patch 25 4 patch 26 4 patch-set patch 21 5 patch 22 5 patch 23 5 patch 24 5 patch 25 5 patch 26 5) [
    set pcolor red
    set is-classroom 0 set is-bathroom 0 set is-staffroom 1 set is-hallway 0 set is-wall 0
    set location "staffroom"
  ]
end

to set-agents
  set student-ctr num-students
  set prof-ctr num-professors
  set staff-ctr num-staff
  set chance-interact random 100
;  create-students num-students [
;    set shape "student"
;    ifelse one-way?[
;      setxy random-patch-entry 0
;    ][
;    ifelse coin-flip?
;      [setxy random-patch-entry 0][setxy random-patch-entry -1]
;    ]
;    ifelse randomize < chance-female [ set gender "F" ] [ set gender "M" ]
;    ifelse randomize < chance-young [ set age "Y" ] [ set age "O" ]
;  ]
;
;  create-professors num-professors [
;    set shape "professor"
;    let tempx random-xcor
;    let tempy random-ycor
;
;    ifelse ([is-hallway] of patch-at tempx tempy) = 1 [
;      setxy tempx tempy
;    ] [
;      while [ ([is-hallway] of patch-at tempx tempy) = 0 ] [
;        set tempx random-xcor
;        set tempy random-ycor
;      ]
;    ]
;
;    setxy tempx tempy
;    ifelse randomize < chance-female [ set gender "F" ] [ set gender "M" ]
;    ifelse randomize < chance-young [ set age "Y" ] [ set age "O" ]
;  ]
;
;  create-staffs num-staff [
;    set shape "staff"
;    let tempx random-xcor
;    let tempy random-ycor
;
;    ifelse ([is-hallway] of patch-at tempx tempy) = 1 [
;      setxy tempx tempy
;    ] [
;      while [ ([is-hallway] of patch-at tempx tempy) = 0 ] [
;        set tempx random-xcor
;        set tempy random-ycor
;      ]
;    ]
;
;    setxy tempx tempy
;    ifelse randomize < chance-female [ set gender "F" ] [ set gender "M" ]
;    ifelse randomize < chance-young [ set age "Y" ] [ set age "O" ]
;  ]
;
  ask turtles [
    set interaction-time 60
    set label insert-item 0 label "    "
    set label insert-item 0 label gender
    set label insert-item 1 label age

    set label-color black
  ]
end

to set-destinations
  ; similar code for each shape
  ask turtles with [shape = "student"]
  [
    let myrandom1 random-float 1
    let myrandom2 random-float 1
    let myrandom3 random-float 1

    ; For bottom right classroom
    ifelse myrandom1 <= 0.167 [ set class1 "br" ] [
    ; For middle right classroom
    ifelse myrandom1 <= 0.333 [ set class1 "mr" ] [
    ; For top right classroom
    ifelse myrandom1 <= 0.500 [ set class1 "tr" ] [
    ; For bottom left classroom
    ifelse myrandom1 <= 0.667 [ set class1 "bl" ] [
    ; For middle left classroom
    ifelse myrandom1 <= 0.833 [ set class1 "ml" ] [
    ; For top left classroom
    set class1 "tl" ]]]]]

    ; For bottom right classroom
    ifelse myrandom2 <= 0.167 [ set class2 "br" ] [
    ; For middle right classroom
    ifelse myrandom2 <= 0.333 [ set class2 "mr" ] [
    ; For top right classroom
    ifelse myrandom2 <= 0.500 [ set class2 "tr" ] [
    ; For bottom left classroom
    ifelse myrandom2 <= 0.667 [ set class2 "bl" ] [
    ; For middle left classroom
    ifelse myrandom2 <= 0.833 [ set class2 "ml" ] [
    ; For top left classroom
    set class2 "tl" ]]]]]

    ; For bottom right classroom
    ifelse myrandom3 <= 0.167 [ set class3 "br" ] [
    ; For middle right classroom
    ifelse myrandom3 <= 0.333 [ set class3 "mr" ] [
    ; For top right classroom
    ifelse myrandom3 <= 0.500 [ set class3 "tr" ] [
    ; For bottom left classroom
    ifelse myrandom3 <= 0.667 [ set class3 "bl" ] [
    ; For middle left classroom
    ifelse myrandom3 <= 0.833 [ set class3 "ml" ] [
    ; For top left classroom
    set class3 "tl" ]]]]]
  ]
end

to go
  let entrances randomize-num-entrance
  ;let _patches (patches with [is-classroom = 1])

  ;if shape = "student" [
    ;if (ticks >= 50) and (ticks <= 250) [
      ;if patch-here != destination [
        ;face one-of patches with [is-classroom = 1]
        ;forward ((random-float forward-movement-range) / 5)
        ;move-to one-of (_patches with [not any? turtles-here])
      ;]
    ;]
  ;]

  ; Go to classroom if haven't reached
  if (ticks >= 200) and (ticks <= 600) [
    ask turtles with [class1 != location] [
      if (class1 != 0) and (class1 != location) [
        face min-one-of patches with [location = [class1] of one-of turtles with [class1 != location]][distance myself]
        move
      ]
    ]
  ]

  if (ticks >= 800) and (ticks <= 1200) [
    ask turtles with [class2 != location] [
      if (class2 != 0) and (class2 != location) [
        face min-one-of patches with [location = [class2] of one-of turtles with [class2 != location]][distance myself]
        move
      ]
    ]
  ]

  if (ticks >= 1400) and (ticks <= 1800) [
    ask turtles with [class3 != location] [
      if (class3 != 0) and (class3 != location) [
        face min-one-of patches with [location = [class3] of one-of turtles with [class3 != location]][distance myself]
        move
      ]
    ]
  ]

  ask turtles [
    ; Movement Part
    ;if [ pcolor ] of patch-ahead 1 = gray [
    ; set heading heading + 180
    ;]

    ;let candidate-heading (random-float (2 * heading-range + 1) - heading-range)
    ;let candidate-movement ((random-float forward-movement-range) / 5)

    ;right candidate-heading
    ;let candidate-patch patch-ahead candidate-movement

    ;forward candidate-movement

    ; Interaction Part

    ifelse any? (other turtles in-cone vision-nonverbal angle-nonverbal) and chance-interact > 97 [
      set num-nonverbal num-nonverbal + 1
      if shape = "student" [
        ask other turtles in-cone vision-nonverbal angle-nonverbal[
          if shape = "professor" [
            set num-student-prof num-student-prof + 1
          ]
          if shape = "student"[
            set num-student-student num-student-student + 1
          ]
          if shape = "staff"[
            set num-student-staff num-student-staff + 1
          ]
        ]
      ]
      if shape = "professor" [
        ask other turtles in-cone vision-nonverbal angle-nonverbal[
          if shape = "professor" [
            set num-prof-prof num-student-prof + 1
          ]
          if shape = "student"[
            set num-student-prof num-student-prof + 1
          ]
          if shape = "staff"[
            set num-prof-staff num-prof-staff + 1
          ]
        ]
      ]
      if shape = "staff" [
        ask other turtles in-cone vision-nonverbal angle-nonverbal[
          if shape = "staff" [
            set num-staff-staff num-staff-staff + 1
          ]
          if shape = "student"[
            set num-student-staff num-student-staff + 1
          ]
          if shape = "professor"[
            set num-prof-staff num-prof-staff + 1
          ]
        ]
      ]
      set interaction-time interaction-time - 1
      if interaction-time = 0 [
        set interaction-time 60
        set chance-interact random 100
        move
      ]
    ] [
      ifelse any? (other turtles in-cone vision-cooperative angle-cooperative) and chance-interact > 95 [
        set num-cooperative num-cooperative + 1
        if shape = "student" [
          ask other turtles in-cone vision-cooperative angle-cooperative[
            if shape = "professor" [
              set num-student-prof num-student-prof + 1
            ]
            if shape = "student"[
              set num-student-student num-student-student + 1
            ]
            if shape = "staff"[
              set num-student-staff num-student-staff + 1
            ]
          ]
        ]
        if shape = "professor" [
          ask other turtles in-cone vision-cooperative angle-cooperative[
            if shape = "professor" [
              set num-prof-prof num-student-prof + 1
            ]
            if shape = "student"[
              set num-student-prof num-student-prof + 1
            ]
            if shape = "staff"[
              set num-prof-staff num-prof-staff + 1
            ]
          ]
        ]
        if shape = "staff" [
          ask other turtles in-cone vision-cooperative angle-cooperative[
            if shape = "staff" [
              set num-staff-staff num-staff-staff + 1
            ]
            if shape = "student"[
              set num-student-staff num-student-staff + 1
            ]
            if shape = "professor"[
              set num-prof-staff num-prof-staff + 1
            ]
          ]
        ]
        set interaction-time interaction-time - 1
        if interaction-time = 0 [
          set interaction-time 60
          set chance-interact random 100
          move
        ]
      ] [
        ifelse any? (other turtles in-cone vision-exchange angle-exchange) and chance-interact > 90 [
          set num-exchange num-exchange + 1
          if shape = "student" [
            ask other turtles in-cone vision-exchange angle-exchange[
              if shape = "professor" [
                set num-student-prof num-student-prof + 1
              ]
              if shape = "student"[
                set num-student-student num-student-student + 1
              ]
              if shape = "staff"[
                set num-student-staff num-student-staff + 1
              ]
            ]
          ]
          if shape = "professor" [
            ask other turtles in-cone vision-exchange angle-exchange[
              if shape = "professor" [
                set num-prof-prof num-student-prof + 1
              ]
              if shape = "student"[
                set num-student-prof num-student-prof + 1
              ]
              if shape = "staff"[
                set num-prof-staff num-prof-staff + 1
              ]
            ]
          ]
          if shape = "staff" [
            ask other turtles in-cone vision-exchange angle-exchange[
              if shape = "staff" [
                set num-staff-staff num-staff-staff + 1
              ]
              if shape = "student"[
                set num-student-staff num-student-staff + 1
              ]
              if shape = "professor"[
                set num-prof-staff num-prof-staff + 1
              ]
            ]
          ]
          set interaction-time interaction-time - 1
          if interaction-time = 0 [
            set interaction-time 60
            set chance-interact random 100
            move
          ]
        ] [
          set chance-interact random 100
          move
        ]
      ]
    ]
  ]
  repeat entrances [
    let agent-type random-type student-ctr prof-ctr staff-ctr
    print agent-type
    if agent-type = 0
    [
      if student-ctr > 0 [
        set student-ctr student-ctr - 1
        create-students 1 [
          set shape "student"
          ifelse one-way?[
            setxy random-patch-entry 0
          ][
            ifelse coin-flip?
            [setxy random-patch-entry 0][setxy random-patch-entry -1]
          ]
          ifelse randomize < chance-female [ set gender "F" ] [ set gender "M" ]
          ifelse randomize < chance-young [ set age "Y" ] [ set age "O" ]

          set interaction-time 60
          set label insert-item 0 label "    "
          set label insert-item 0 label gender
          set label insert-item 1 label age
          set label-color black

          ask turtles with [shape = "student"]
          [
            let myrandom1 random-float 1
            let myrandom2 random-float 1
            let myrandom3 random-float 1

            ; For bottom right classroom
            ifelse myrandom1 <= 0.167 [ set class1 "br" ] [
            ; For middle right classroom
            ifelse myrandom1 <= 0.333 [ set class1 "mr" ] [
            ; For top right classroom
            ifelse myrandom1 <= 0.500 [ set class1 "tr" ] [
            ; For bottom left classroom
            ifelse myrandom1 <= 0.667 [ set class1 "bl" ] [
            ; For middle left classroom
            ifelse myrandom1 <= 0.833 [ set class1 "ml" ] [
            ; For top left classroom
            set class1 "tl" ]]]]]

            ; For bottom right classroom
            ifelse myrandom2 <= 0.167 [ set class2 "br" ] [
            ; For middle right classroom
            ifelse myrandom2 <= 0.333 [ set class2 "mr" ] [
            ; For top right classroom
            ifelse myrandom2 <= 0.500 [ set class2 "tr" ] [
            ; For bottom left classroom
            ifelse myrandom2 <= 0.667 [ set class2 "bl" ] [
            ; For middle left classroom
            ifelse myrandom2 <= 0.833 [ set class2 "ml" ] [
            ; For top left classroom
            set class2 "tl" ]]]]]

            ; For bottom right classroom
            ifelse myrandom3 <= 0.167 [ set class3 "br" ] [
            ; For middle right classroom
            ifelse myrandom3 <= 0.333 [ set class3 "mr" ] [
            ; For top right classroom
            ifelse myrandom3 <= 0.500 [ set class3 "tr" ] [
            ; For bottom left classroom
            ifelse myrandom3 <= 0.667 [ set class3 "bl" ] [
            ; For middle left classroom
            ifelse myrandom3 <= 0.833 [ set class3 "ml" ] [
            ; For top left classroom
            set class3 "tl" ]]]]]
          ]
        ]
      ]
    ]
    if agent-type = 1
    [

      if prof-ctr > 0 [
        set prof-ctr prof-ctr - 1

        create-professors 1 [
          set shape "professor"
          ifelse one-way?[
            setxy random-patch-entry 0
          ][
            ifelse coin-flip?
            [setxy random-patch-entry 0][setxy random-patch-entry -1]
          ]
          ifelse randomize < chance-female [ set gender "F" ] [ set gender "M" ]
          ifelse randomize < chance-young [ set age "Y" ] [ set age "O" ]

          set interaction-time 60
          set label insert-item 0 label "    "
          set label insert-item 0 label gender
          set label insert-item 1 label age
          set label-color black
        ]
      ]
    ]
    if agent-type = 2
    [

      if staff-ctr > 0 [
        set staff-ctr staff-ctr - 1

        create-staffs 1 [
          set shape "staff"
          ifelse one-way?[
            setxy random-patch-entry 0
          ][
            ifelse coin-flip?
            [setxy random-patch-entry 0][setxy random-patch-entry -1]
          ]
          ifelse randomize < chance-female [ set gender "F" ] [ set gender "M" ]
          ifelse randomize < chance-young [ set age "Y" ] [ set age "O" ]

          set interaction-time 60
          set label insert-item 0 label "    "
          set label insert-item 0 label gender
          set label insert-item 1 label age
          set label-color black
        ]
      ]
    ]

  ]

  tick
end

to move
  if [ pcolor ] of patch-ahead 1 = gray [
   set heading heading + 180
  ]

  let candidate-heading (random-float (2 * heading-range + 1) - heading-range)
  let candidate-movement ((random-float forward-movement-range) / 5)

  right candidate-heading
  let candidate-patch patch-ahead candidate-movement

  forward candidate-movement
end

to-report randomize
  report random 100
end

to-report coin-flip?
  report random 2 = 0
end

to-report randomize-num-entrance
  report random num-entrance
end

to-report random-type [a b c]
  let value random a + b + c
  print a
  print b
  print c
  print "----"
  if value < a [report 0]
  if value < (a + b) [report 1]
  if value < (a + b + c) [report 2]
  report -1
end

to-report random-patch-entry
  report one-of [11 12 13 14 15 16 17 18 19 20]
end
@#$#@#$#@
GRAPHICS-WINDOW
246
24
879
658
-1
-1
19.55
1
10
1
1
1
0
0
1
1
0
31
0
31
0
0
1
ticks
30.0

SLIDER
62
80
236
113
num-students
num-students
0
200
30.0
1
1
NIL
HORIZONTAL

SLIDER
62
123
236
156
num-professors
num-professors
0
100
0.0
1
1
NIL
HORIZONTAL

SLIDER
62
166
236
199
num-staff
num-staff
0
50
0.0
1
1
NIL
HORIZONTAL

SLIDER
62
292
236
325
num-open
num-open
0
6
3.0
1
1
NIL
HORIZONTAL

SLIDER
62
37
236
70
spawn-tick-interval
spawn-tick-interval
0
100
50.0
1
1
NIL
HORIZONTAL

BUTTON
174
467
237
500
NIL
go
T
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
152
509
237
542
go-once
go
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

BUTTON
170
424
236
457
NIL
setup
NIL
1
T
OBSERVER
NIL
NIL
NIL
NIL
1

CHOOSER
62
369
237
414
entrance-mode
entrance-mode
"one-way" "two-way"
0

MONITOR
887
34
1036
79
NIL
num-student-student
0
1
11

MONITOR
887
89
1036
134
NIL
num-student-prof
0
1
11

MONITOR
887
144
1036
189
NIL
num-student-staff
0
1
11

MONITOR
887
199
1036
244
NIL
num-prof-prof
0
1
11

MONITOR
887
254
1036
299
NIL
num-prof-staff
0
1
11

MONITOR
887
309
1036
354
NIL
num-staff-staff
0
1
11

SLIDER
62
209
236
242
chance-female
chance-female
0
100
50.0
1
1
%
HORIZONTAL

SLIDER
62
250
236
283
chance-young
chance-young
0
100
50.0
1
1
%
HORIZONTAL

MONITOR
887
364
1036
409
NIL
num-nonverbal
0
1
11

MONITOR
887
418
1036
463
NIL
num-cooperative
0
1
11

MONITOR
887
473
1037
518
NIL
num-exchange
0
1
11

SLIDER
64
330
237
363
num-entrance
num-entrance
1
5
2.0
1
1
NIL
HORIZONTAL

@#$#@#$#@
## WHAT IS IT?

(a general understanding of what the model is trying to show or explain)

## HOW IT WORKS

(what rules the agents use to create the overall behavior of the model)

## HOW TO USE IT

(how to use the model, including a description of each of the items in the Interface tab)

## THINGS TO NOTICE

(suggested things for the user to notice while running the model)

## THINGS TO TRY

(suggested things for the user to try to do (move sliders, switches, etc.) with the model)

## EXTENDING THE MODEL

(suggested things to add or change in the Code tab to make the model more complicated, detailed, accurate, etc.)

## NETLOGO FEATURES

(interesting or unusual features of NetLogo that the model uses, particularly in the Code tab; or where workarounds were needed for missing features)

## RELATED MODELS

(models in the NetLogo Models Library and elsewhere which are of related interest)

## CREDITS AND REFERENCES

(a reference to the model's URL on the web if it has one, as well as any other necessary credits, citations, and links)
@#$#@#$#@
default
true
0
Polygon -7500403 true true 150 5 40 250 150 205 260 250

airplane
true
0
Polygon -7500403 true true 150 0 135 15 120 60 120 105 15 165 15 195 120 180 135 240 105 270 120 285 150 270 180 285 210 270 165 240 180 180 285 195 285 165 180 105 180 60 165 15

arrow
true
0
Polygon -7500403 true true 150 0 0 150 105 150 105 293 195 293 195 150 300 150

box
false
0
Polygon -7500403 true true 150 285 285 225 285 75 150 135
Polygon -7500403 true true 150 135 15 75 150 15 285 75
Polygon -7500403 true true 15 75 15 225 150 285 150 135
Line -16777216 false 150 285 150 135
Line -16777216 false 150 135 15 75
Line -16777216 false 150 135 285 75

bug
true
0
Circle -7500403 true true 96 182 108
Circle -7500403 true true 110 127 80
Circle -7500403 true true 110 75 80
Line -7500403 true 150 100 80 30
Line -7500403 true 150 100 220 30

butterfly
true
0
Polygon -7500403 true true 150 165 209 199 225 225 225 255 195 270 165 255 150 240
Polygon -7500403 true true 150 165 89 198 75 225 75 255 105 270 135 255 150 240
Polygon -7500403 true true 139 148 100 105 55 90 25 90 10 105 10 135 25 180 40 195 85 194 139 163
Polygon -7500403 true true 162 150 200 105 245 90 275 90 290 105 290 135 275 180 260 195 215 195 162 165
Polygon -16777216 true false 150 255 135 225 120 150 135 120 150 105 165 120 180 150 165 225
Circle -16777216 true false 135 90 30
Line -16777216 false 150 105 195 60
Line -16777216 false 150 105 105 60

car
false
0
Polygon -7500403 true true 300 180 279 164 261 144 240 135 226 132 213 106 203 84 185 63 159 50 135 50 75 60 0 150 0 165 0 225 300 225 300 180
Circle -16777216 true false 180 180 90
Circle -16777216 true false 30 180 90
Polygon -16777216 true false 162 80 132 78 134 135 209 135 194 105 189 96 180 89
Circle -7500403 true true 47 195 58
Circle -7500403 true true 195 195 58

circle
false
0
Circle -7500403 true true 0 0 300

circle 2
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240

cow
false
0
Polygon -7500403 true true 200 193 197 249 179 249 177 196 166 187 140 189 93 191 78 179 72 211 49 209 48 181 37 149 25 120 25 89 45 72 103 84 179 75 198 76 252 64 272 81 293 103 285 121 255 121 242 118 224 167
Polygon -7500403 true true 73 210 86 251 62 249 48 208
Polygon -7500403 true true 25 114 16 195 9 204 23 213 25 200 39 123

cylinder
false
0
Circle -7500403 true true 0 0 300

dot
false
0
Circle -7500403 true true 90 90 120

face happy
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 255 90 239 62 213 47 191 67 179 90 203 109 218 150 225 192 218 210 203 227 181 251 194 236 217 212 240

face neutral
false
0
Circle -7500403 true true 8 7 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Rectangle -16777216 true false 60 195 240 225

face sad
false
0
Circle -7500403 true true 8 8 285
Circle -16777216 true false 60 75 60
Circle -16777216 true false 180 75 60
Polygon -16777216 true false 150 168 90 184 62 210 47 232 67 244 90 220 109 205 150 198 192 205 210 220 227 242 251 229 236 206 212 183

fish
false
0
Polygon -1 true false 44 131 21 87 15 86 0 120 15 150 0 180 13 214 20 212 45 166
Polygon -1 true false 135 195 119 235 95 218 76 210 46 204 60 165
Polygon -1 true false 75 45 83 77 71 103 86 114 166 78 135 60
Polygon -7500403 true true 30 136 151 77 226 81 280 119 292 146 292 160 287 170 270 195 195 210 151 212 30 166
Circle -16777216 true false 215 106 30

flag
false
0
Rectangle -7500403 true true 60 15 75 300
Polygon -7500403 true true 90 150 270 90 90 30
Line -7500403 true 75 135 90 135
Line -7500403 true 75 45 90 45

flower
false
0
Polygon -10899396 true false 135 120 165 165 180 210 180 240 150 300 165 300 195 240 195 195 165 135
Circle -7500403 true true 85 132 38
Circle -7500403 true true 130 147 38
Circle -7500403 true true 192 85 38
Circle -7500403 true true 85 40 38
Circle -7500403 true true 177 40 38
Circle -7500403 true true 177 132 38
Circle -7500403 true true 70 85 38
Circle -7500403 true true 130 25 38
Circle -7500403 true true 96 51 108
Circle -16777216 true false 113 68 74
Polygon -10899396 true false 189 233 219 188 249 173 279 188 234 218
Polygon -10899396 true false 180 255 150 210 105 210 75 240 135 240

house
false
0
Rectangle -7500403 true true 45 120 255 285
Rectangle -16777216 true false 120 210 180 285
Polygon -7500403 true true 15 120 150 15 285 120
Line -16777216 false 30 120 270 120

leaf
false
0
Polygon -7500403 true true 150 210 135 195 120 210 60 210 30 195 60 180 60 165 15 135 30 120 15 105 40 104 45 90 60 90 90 105 105 120 120 120 105 60 120 60 135 30 150 15 165 30 180 60 195 60 180 120 195 120 210 105 240 90 255 90 263 104 285 105 270 120 285 135 240 165 240 180 270 195 240 210 180 210 165 195
Polygon -7500403 true true 135 195 135 240 120 255 105 255 105 285 135 285 165 240 165 195

line
true
0
Line -7500403 true 150 0 150 300

line half
true
0
Line -7500403 true 150 0 150 150

pentagon
false
0
Polygon -7500403 true true 150 15 15 120 60 285 240 285 285 120

person
false
0
Circle -7500403 true true 110 5 80
Polygon -7500403 true true 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Rectangle -7500403 true true 127 79 172 94
Polygon -7500403 true true 195 90 240 150 225 180 165 105
Polygon -7500403 true true 105 90 60 150 75 180 135 105

plant
false
0
Rectangle -7500403 true true 135 90 165 300
Polygon -7500403 true true 135 255 90 210 45 195 75 255 135 285
Polygon -7500403 true true 165 255 210 210 255 195 225 255 165 285
Polygon -7500403 true true 135 180 90 135 45 120 75 180 135 210
Polygon -7500403 true true 165 180 165 210 225 180 255 120 210 135
Polygon -7500403 true true 135 105 90 60 45 45 75 105 135 135
Polygon -7500403 true true 165 105 165 135 225 105 255 45 210 60
Polygon -7500403 true true 135 90 120 45 150 15 180 45 165 90

professor
false
0
Rectangle -1 true false 120 90 180 180
Polygon -16777216 true false 135 90 150 105 135 180 150 195 165 180 150 105 165 90
Polygon -14835848 true false 120 90 105 90 60 195 90 210 116 154 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 183 153 210 210 240 195 195 90 180 90 150 165
Circle -6459832 true false 110 5 80
Rectangle -6459832 true false 127 76 172 91
Line -16777216 false 172 90 161 94
Line -16777216 false 128 90 139 94
Polygon -16777216 true false 195 225 195 300 270 270 270 195
Rectangle -7500403 true true 180 225 195 300
Polygon -7500403 true true 180 226 195 226 270 196 255 196
Polygon -16777216 true false 209 202 209 216 244 202 243 188
Line -16777216 false 180 90 150 165
Line -16777216 false 120 90 150 165

sheep
false
15
Circle -1 true true 203 65 88
Circle -1 true true 70 65 162
Circle -1 true true 150 105 120
Polygon -7500403 true false 218 120 240 165 255 165 278 120
Circle -7500403 true false 214 72 67
Rectangle -1 true true 164 223 179 298
Polygon -1 true true 45 285 30 285 30 240 15 195 45 210
Circle -1 true true 3 83 150
Rectangle -1 true true 65 221 80 296
Polygon -1 true true 195 285 210 285 210 240 240 210 195 210
Polygon -7500403 true false 276 85 285 105 302 99 294 83
Polygon -7500403 true false 219 85 210 105 193 99 201 83

square
false
0
Rectangle -7500403 true true 30 30 270 270

square 2
false
0
Rectangle -7500403 true true 30 30 270 270
Rectangle -16777216 true false 60 60 240 240

staff
false
0
Rectangle -6459832 true false 123 76 176 95
Polygon -1 true false 105 90 60 195 90 210 115 162 184 163 210 210 240 195 195 90
Polygon -13345367 true false 180 195 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285
Circle -6459832 true false 110 5 80
Line -16777216 false 148 143 150 196
Rectangle -16777216 true false 116 186 182 198
Circle -1 true false 152 143 9
Circle -1 true false 152 166 9
Rectangle -16777216 true false 179 164 183 186
Polygon -955883 true false 180 90 195 90 195 165 195 195 150 195 150 120 180 90
Polygon -955883 true false 120 90 105 90 105 165 105 195 150 195 150 120 120 90
Rectangle -16777216 true false 135 114 150 120
Rectangle -16777216 true false 135 144 150 150
Rectangle -16777216 true false 135 174 150 180
Polygon -955883 true false 105 42 111 16 128 2 149 0 178 6 190 18 192 28 220 29 216 34 201 39 167 35
Polygon -7500403 true true 54 253 54 238 219 73 227 78
Polygon -7500403 true true 15 285 15 255 30 225 45 225 75 255 75 270 45 285

star
false
0
Polygon -7500403 true true 151 1 185 108 298 108 207 175 242 282 151 216 59 282 94 175 3 108 116 108

student
false
0
Polygon -13791810 true false 135 90 150 105 135 165 150 180 165 165 150 105 165 90
Polygon -6459832 true false 195 90 240 195 210 210 165 105
Circle -6459832 true false 110 5 80
Rectangle -6459832 true false 127 79 172 94
Polygon -13345367 true false 105 90 120 195 90 285 105 300 135 300 150 225 165 300 195 300 210 285 180 195 195 90
Polygon -1 true false 100 210 130 225 145 165 85 135 63 189
Polygon -13791810 true false 90 210 120 225 135 165 67 130 53 189
Polygon -1 true false 120 224 131 225 124 210
Line -16777216 false 139 168 126 225
Line -16777216 false 140 167 76 136
Polygon -6459832 true false 105 90 60 195 90 210 135 105

target
false
0
Circle -7500403 true true 0 0 300
Circle -16777216 true false 30 30 240
Circle -7500403 true true 60 60 180
Circle -16777216 true false 90 90 120
Circle -7500403 true true 120 120 60

tree
false
0
Circle -7500403 true true 118 3 94
Rectangle -6459832 true false 120 195 180 300
Circle -7500403 true true 65 21 108
Circle -7500403 true true 116 41 127
Circle -7500403 true true 45 90 120
Circle -7500403 true true 104 74 152

triangle
false
0
Polygon -7500403 true true 150 30 15 255 285 255

triangle 2
false
0
Polygon -7500403 true true 150 30 15 255 285 255
Polygon -16777216 true false 151 99 225 223 75 224

truck
false
0
Rectangle -7500403 true true 4 45 195 187
Polygon -7500403 true true 296 193 296 150 259 134 244 104 208 104 207 194
Rectangle -1 true false 195 60 195 105
Polygon -16777216 true false 238 112 252 141 219 141 218 112
Circle -16777216 true false 234 174 42
Rectangle -7500403 true true 181 185 214 194
Circle -16777216 true false 144 174 42
Circle -16777216 true false 24 174 42
Circle -7500403 false true 24 174 42
Circle -7500403 false true 144 174 42
Circle -7500403 false true 234 174 42

turtle
true
0
Polygon -10899396 true false 215 204 240 233 246 254 228 266 215 252 193 210
Polygon -10899396 true false 195 90 225 75 245 75 260 89 269 108 261 124 240 105 225 105 210 105
Polygon -10899396 true false 105 90 75 75 55 75 40 89 31 108 39 124 60 105 75 105 90 105
Polygon -10899396 true false 132 85 134 64 107 51 108 17 150 2 192 18 192 52 169 65 172 87
Polygon -10899396 true false 85 204 60 233 54 254 72 266 85 252 107 210
Polygon -7500403 true true 119 75 179 75 209 101 224 135 220 225 175 261 128 261 81 224 74 135 88 99

wheel
false
0
Circle -7500403 true true 3 3 294
Circle -16777216 true false 30 30 240
Line -7500403 true 150 285 150 15
Line -7500403 true 15 150 285 150
Circle -7500403 true true 120 120 60
Line -7500403 true 216 40 79 269
Line -7500403 true 40 84 269 221
Line -7500403 true 40 216 269 79
Line -7500403 true 84 40 221 269

wolf
false
0
Polygon -16777216 true false 253 133 245 131 245 133
Polygon -7500403 true true 2 194 13 197 30 191 38 193 38 205 20 226 20 257 27 265 38 266 40 260 31 253 31 230 60 206 68 198 75 209 66 228 65 243 82 261 84 268 100 267 103 261 77 239 79 231 100 207 98 196 119 201 143 202 160 195 166 210 172 213 173 238 167 251 160 248 154 265 169 264 178 247 186 240 198 260 200 271 217 271 219 262 207 258 195 230 192 198 210 184 227 164 242 144 259 145 284 151 277 141 293 140 299 134 297 127 273 119 270 105
Polygon -7500403 true true -1 195 14 180 36 166 40 153 53 140 82 131 134 133 159 126 188 115 227 108 236 102 238 98 268 86 269 92 281 87 269 103 269 113

x
false
0
Polygon -7500403 true true 270 75 225 30 30 225 75 270
Polygon -7500403 true true 30 75 75 30 270 225 225 270
@#$#@#$#@
NetLogo 6.2.0
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
@#$#@#$#@
default
0.0
-0.2 0 0.0 1.0
0.0 1 1.0 0.0
0.2 0 0.0 1.0
link direction
true
0
Line -7500403 true 150 150 90 180
Line -7500403 true 150 150 210 180
@#$#@#$#@
0
@#$#@#$#@
