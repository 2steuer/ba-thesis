set term pdf size 10cm,6cm
set output "3pointers_center.pdf"

set xrange [0:1500]
set xlabel "x"
set xtics 200
set size ratio -1

set yrange [900:0] reverse
set ylabel "y"

set xzeroaxis
set yzeroaxis
plot '3touchpoints_vectors.dat' with vectors filled head lc black notitle, \
		 '3touchpoints_center.dat' using 1:2:(sprintf("%s (%.1f, %.1f)", stringcolumn(3), $1, $2)) with labels point pt 3 offset char 7,1  notitle, \
		 '3touchpoints_circle.dat' using 1:2:3 with circles notitle
