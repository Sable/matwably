% d -> [DOUBLE&1*1&REAL]
function [ a ] = returnAlias4(d)
    a = rand(2,2)
    c = a % a = rand(2,2) {refC:2}
    if d < rand()
        a = rand(2,2)
        % {a = rand(2,2)} {refC:1}
        e = c
        f = c % {c = rand(2,2) {refC:3}}
    end
    % Diverging static info, initiate RC along each path.
    % From here, variables  a,c,f, and e are dynamic.
    b = a
    % Decrease 'b' reference, increase 'a' reference.
    % tag a as return value,  try to free b
    % by checking return flag. Return 'a' site.
end

