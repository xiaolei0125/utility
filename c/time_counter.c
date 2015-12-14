
S3G_ACCESS_GPU_ALMOST

rxaPixmapPriv* rxaCreatePixmapFromHeap(ScreenPtr pScreen, int w, int h, int bpp, int* stride)


#include<stdio.h>
#include<time.h>
#include<stdlib.h>

void time_counter(void)
{
    struct timeval tpstart,tpend;
    float timeuse=0;
	static float timesum=0;
	static int timer_on=0;
	static int timer_save=0;
	static int enter_cnt=0;
	
	if(timer_on)
	{
        gettimeofday(&tpstart,NULL);
	    enter_cnt++
	}

    int k;
    for(k = 0; k < 100000000; ++k);

	if(timer_on)
	{
        gettimeofday(&tpend,NULL);
        timeuse=1000000*(tpend.tv_sec-tpstart.tv_sec)+
        tpend.tv_usec-tpstart.tv_usec;
        timeuse/=1000000;
	    //printf("Used Time:%f\n",timeuse);
	    timesum = timesum + timeuse;
		
		if(timer_save)
			printf("Used Totlal Time:%f\n",timesum);
		//xf86DrvMsg(0, X_INFO, "Function %s: "fmt"\n", __FUNCTION__,##args)
    }
	
    return;
}

int main()
{
    int i;
	for(i=0;i++;i<100)
    	time_counter();

    return 0;
}
