function invalid_return1(g)
    [a,b,c] = invalid_return1_helper(); % Should throw error since be is never defined in the program.
end