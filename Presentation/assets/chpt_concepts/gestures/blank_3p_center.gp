set term pdf size 10cm,6cm
set output "3pointers_center.pdf"

set x2range [0:1500]
set x2label "x"
set x2tics 200
set size ratio -1

set yrange [900:0] reverse
set ylabel "y"

unset xrange
unset xtics
unset xlabel

set xzeroaxis
set yzeroaxis
plot '3touchpoints_vectors.dat' with vectors filled head lc black notitle axes x2y1, \
		 '3touchpoints_center.dat' using 1:2:(sprintf("%s (%.1f, %.1f)", stringcolumn(3), $1, $2)) with labels point pt 3 offset char 7,1  notitle axes x2y1, \
		 '3touchpoints_circle.dat' using 1:2:3 with circles notitle axes x2y1
