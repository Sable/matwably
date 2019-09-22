function [D] = thesis1(k)
    A = randn(2,2)
    B = randn(2,2)
    C = 2+A
    F = C
    if B
        H = ones(2,2)
        F = 2*H
    end
    E = F * A
    D = E
    % return
end