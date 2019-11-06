function merge_op3(s)
C = ones(2,2)
A = C
if rand() < 0.5
    C = rand(2,2)
    A = C
end
if rand() < 0.5
    C = zeros(2,2)
end
end