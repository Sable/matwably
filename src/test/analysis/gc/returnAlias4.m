function [ a ] = returnAlias4(b)
    % return alias dynamic, both sites
    % Copy Analysis should create clones
    a = rand(2,2)
    c = a % a = rand(2,2) {refC:2}
    if b < rand()
        a = rand(2,2)
        f = c
        d = c % a = rand(2,2) {refC:3}
    end
    b = a
    % Dynamically: should check if a is internal set return flag to 1, if external do nothing.
    % When processing whether to free b, check return flag.
end