function [ a ] = returnAlias4(b)
    % return alias dynamic, both sites
    % Copy Analysis should create clones
    a = rand(2,2)
    if b < rand()
        a = rand(2,2)
    end
    b = a
    % Dynamically: should check if a, if a is internal set return flag to 1, if external do nothing.
    % When processing whether to free b, check return flag.
end