#include <ctype.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>

typedef struct item_t {
    char *key;
    char *value;
}ITEM;

char *strtrimr(char *pstr)
{
    int i;
    i = strlen(pstr) - 1;
    while (isspace(pstr[i]) && (i >= 0))
        pstr[i--] = '\0';
    return pstr;
}

char *strtriml(char *pstr)
{
    int i = 0,j;
    j = strlen(pstr) - 1;
    while (isspace(pstr[i]) && (i <= j))
        i++;
    if (0<i)
        strcpy(pstr, &pstr[i]);
    return pstr;
}

char *strtrimm(char *pstr)
{
    int i = 0,j;
    int k=0;
    char buf[1024];
    j = strlen(pstr) - 1;
    if(j >= 1024)
        return pstr;
    
    for(i=0; i<=j; i++)
    {
        if(!isspace(pstr[i]))
        {
            buf[k++] = pstr[i];
        }
    }
    buf[k] = '\0';
    strcpy(pstr, buf);
    return pstr;
}

char *strtrim(char *pstr)
{
    char *p, *p2;
    p = strtrimr(pstr);
    p2 = strtriml(p);
    return strtrimm(p2);
}


int  get_item_from_line(char *line,   ITEM *item)
{
    char *p = strtrim(line);
    int len = strlen(p);
    
    if(len <= 0)
    {
        return -1;
    }
    else if(p[0]=='#')
    {
        return -2;
    }
    else
    {
        char *p2 = strchr(p, '=');
        if(p2 == NULL)
            return -3;
        
        *p2++ = '\0';
        if(strlen(p2) == 0)
            return -4;
        
        item->key = (char *)malloc(strlen(p) + 1);
        item->value = (char *)malloc(strlen(p2) + 1);
        strcpy(item->key,p);
        strcpy(item->value,p2);
    }
    
    return 0;
}

int read_conf_value(const char *key,  char *value,const char *file)
{
    char line[1024];
    FILE *fp;
    fp = fopen(file,"r");
    if(fp == NULL)
        return -1;

    *value = '\0';
    while (fgets(line, 1023, fp))
    {
        ITEM item;
        if( get_item_from_line(line,&item) )continue;
        if(!strcmp(item.key,key))
        {
            strcpy(value,item.value);

            fclose(fp);
            free(item.key);
            free(item.value);
            return 0;
        }

    }
    return -2;
}

int file_to_items(const char *file,   ITEM *items,   int *num)
{
    char line[1024];
    FILE *fp;
    fp = fopen(file,"r");
    if(fp == NULL)
        return 1;
    int i = 0;
    while(fgets(line, 1023, fp)){
            char *p = strtrim(line);
        int len = strlen(p);
        if(len <= 0){
            continue;
        }
        else if(p[0]=='#'){
            continue;
        }else{
            char *p2 = strchr(p, '=');
            /*这里认为只有key没什么意义*/
            if(p2 == NULL)
                continue;
            *p2++ = '\0';
            items[i].key = (char *)malloc(strlen(p) + 1);
            items[i].value = (char *)malloc(strlen(p2) + 1);
            strcpy(items[i].key,p);
            strcpy(items[i].value,p2);

            i++;
        }
    }
    (*num) = i;
    fclose(fp);
    return 0;
}

int write_conf_value(const char *key,char *value,const char *file)
{
    ITEM items[20];// 假定配置项最多有20个
    int num;//存储从文件读取的有效数目
    file_to_items(file, items, &num);

    int i=0;
    //查找要修改的项
    for(i=0;i<num;i++){
        if(!strcmp(items[i].key, key)){
            items[i].value = value;
            break;
        }
    }

    // 更新配置文件,应该有备份，下面的操作会将文件内容清除
    FILE *fp;
    fp = fopen(file, "w");
    if(fp == NULL)
        return 1;

    i=0;
    for(i=0;i<num;i++){
        fprintf(fp,"%s=%s\n",items[i].key, items[i].value);
        //printf("%s=%s\n",items[i].key, items[i].value);
    }
    fclose(fp);
    //清除工作
/*    i=0;
    for(i=0;i<num;i++){
        free(items[i].key);
        free(items[i].value);
    }*/

    return 0;

}

/*
void main(void)
{
   char value[64];
   int ret = -10;
   ret = read_conf_value("mode",value,"./via_utility.cfg");
   printf("ret=%d, Value = %s \n", ret, value);

   ret = read_conf_value("keys",value,"./via_utility.cfg");
   printf("ret=%d, Value = %s \n", ret, value);

}
*/
#if 0
// 精简版函数，独立功能
int read_conf_value(char *value,const char *file)
{
    char line[1024];
    FILE *fp;
	int i;
	   
    fp = fopen(file,"r");
    if(fp == NULL)
        return -1;
        
    fgets(line, 1023, fp);
    
    char *pEnd = strchr(line, '\r');
	if( pEnd != NULL)
		*pEnd = '\0';
    	
    pEnd = strchr(line, '\n');	
    if( pEnd != NULL)
    {
		*pEnd = '\0';
    }
	else return -1;
	
    i = strlen(line) - 1;
    while (isspace(line[i]) && (i >= 0))
        line[i--] = '\0';
	
	strcpy(value,line);
	if(value[0] == '\0')
		return -1;
	
    fclose(fp);	
    return 0;
}
#endif


