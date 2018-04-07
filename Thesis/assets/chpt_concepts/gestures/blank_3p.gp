set term pdf size 10cm,6cm
set output "3pointers_blank.pdf"

set xrange [0:1500]
set xlabel "x"
set xtics 200
set size ratio -1

set yrange [900:0] reverse
set ylabel "y"

set xzeroaxis
set yzeroaxis

plot '3touchpoints.dat' using 1:2:(sprintf("%s (%.1f, %.1f)", stringcolumn(3), $1, $2)) with labels point pt 3 offset char 5,1  notitle

