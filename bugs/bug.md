# Some bugs found

Index out of bounds for b. Should check before, if empty
it should skip the loop. Same with empty a and c
```
    for v = a:[]:c
        disp(v)
    end
```
