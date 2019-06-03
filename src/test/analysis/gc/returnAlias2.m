function [ a ] = returnAlias2(b)
    % return alias static, both sites
    % Copy Analysis should create clones
    a = rand(2,2)
    b = a
    % should return a, and not free b!
end