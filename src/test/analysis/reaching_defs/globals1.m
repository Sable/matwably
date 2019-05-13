function globals1()
    d = rand();
    disp(d)
    if d > 0.5
        global a;
        a = 4
    end
    disp(a)
    global a
    a = 3;
    disp(a)
    
    
    
    

end