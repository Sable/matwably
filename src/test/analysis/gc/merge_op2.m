function merge_op2(g)
    A = ones(2,2)
    C = A
    if rand() < 0.5
        C = ones(3,3)
    end
    A = ones(3,3)
    C = rand(3,3)
    disp(A+C)
end