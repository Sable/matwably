function binary_reuse(s)
    B = ones(2,2)
    A = zeros(2,2)
    for i=1:10
        A = plus(A, B) % should re-use A site
    end
    disp(A)

end