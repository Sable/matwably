function [ a, b] = returnAlias1(b)
    % return alias static, both sites
    % Copy Analysis should create clones
    a = rand(2,2)
    b = a
end