function array_set1(a)
    a = 30
    a(1) = 4;
    b = a *20; % Can we replace a here by its definition, the answer is no! Since a is modified here.

end