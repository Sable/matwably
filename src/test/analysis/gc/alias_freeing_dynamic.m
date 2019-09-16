function [A] = alias_freeing_dynamic(A)
    B = rand(2,2)

    if B
        B = rand(2,2)
    end

    for i=1:10
        C = ones(2,2)
        D = zeros(2,2)
        E = randn(2,2)
        F = randi(2,2)
        H = eye(2,2)
        N = rand(2,2)
    end
    disp(B)

end