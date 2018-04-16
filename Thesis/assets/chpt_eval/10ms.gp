set terminal pdf size 12cm,9cm
set output '10ms.pdf'

red = "#FF0000"; green = "#00FF00"; blue = "#0000FF"; skyblue = "#87CEEB";
set style line 2 lc rgb 'black' lt 1 lw 1
set style data histogram
set style histogram cluster gap 1
set style fill pattern border -1

set ylabel "ms"
set boxwidth 0.9
set xtics format ""
set grid ytics

unset title
plot "10ms.dat" using 1:xtic(7) title "1 FT, prec." ls 2, \
            '' using 2 title "1 FT, approx." ls 2, \
            '' using 3 title "2 FT, prec." ls 2, \
            '' using 4 title "2 FT, approx." ls 2, \
            '' using 5 title "3 FT, prec." ls 2, \
            '' using 6 title "3 FT, approx." ls 2
