function merge_op(A)
    A = ones(2,2)
    B = A
    if rand() < 0.5
        A = rand(2,2)
        B = A
    end
    disp(A)

    if rand() < 0.5
        A = rand(2,2)
    end
    disp(A)

end