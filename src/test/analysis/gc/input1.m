function input1(A, B)
%   Check if A has any ambiguous uses, if it
    if rand() > 0.5
        A = rand(3,3)
    end
    disp(A)
    % Expect A to be set as external
end