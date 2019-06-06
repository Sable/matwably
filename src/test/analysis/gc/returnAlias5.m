function [ a ] = returnAlias5(b)
    % return alias dynamic, both sites
    % Copy Analysis should create clones
    a = rand(2,2)
    c = a % a = rand(2,2) {refC:2}
    if b < rand()
        a = rand(2,2)
        % a = rand(2,2) {refC:1}
    end
    b = a
    % decrease b reference, increase a reference.
    % tag a as return value,  try to free b but not,
    % by checking return flag. Return a site.
end