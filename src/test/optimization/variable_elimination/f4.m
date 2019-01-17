function f4(a)
    c = 3 + 20 - 5 * 20;
    b = a;
%   if no variable is used, eliminate local, if rhs is a literal, eliminate variable,
%   if function call on the right is pure, eliminate rhs.
    disp(c + a)
end
