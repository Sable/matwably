function [ a, b] = returnAlias3(b)
    % return alias dynamic, both sites
    % Copy Analysis should create clones
    a = rand(2,2)
    if b < rand()
        a = rand(2,2)
    end
    b = a
end