#include<stdio.h>
#include<stdlib.h>
#include<time.h>
#include<math.h>

double mod(double x,double n){
	return  x - floor(x/n)*n; 
}
double collatz(double n){
	double y = 0;
	while( n != 1.0 ){
			if( mod(n,2) == 0){
					n = n / 2.0;
			}else{
					n = 3*n + 1;
			}
		y = y+1;
	}
	return y;
}
void drv_collatz(double scale);
int main(){
	drv_collatz(1000000);
}
void drv_collatz(double scale){
	double max_length = 0;
	double max_num = 0;
	double start = clock();
	for(double i = 1; i <= scale;i++){
			double length = collatz(i);
			if(length > max_length){
					max_length = length;
					max_num = i;
			}
	}
	double end = clock();
	printf("time:%f\n",((double) (end - start)) / CLOCKS_PER_SEC);
	printf("MaxNum: %f\n",max_num);
}
