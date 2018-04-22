set term pdf size 10cm,6cm
set output "3pointers_blank.pdf"

set x2range [0:1500]
set x2label "x"
set x2tics 200
set size ratio -1

unset xlabel
unset xrange
unset xtics

set yrange [900:0] reverse
set ylabel "y"

set xzeroaxis
set yzeroaxis

plot '3touchpoints.dat' using 1:2:(sprintf("%s (%.1f, %.1f)", stringcolumn(3), $1, $2)) with labels point pt 3 offset char 5,1  notitle axes x2y1

