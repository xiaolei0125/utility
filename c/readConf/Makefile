include ../rule.mk
PROGRAM = VIAUtilityServer
all: $(PROGRAM)
VIALDFLAGS = -lpthread -lviagfx -lXrandr -lXv

# To include s3g_def.h and s3g_ioctl.h,
# otherwise needs to copy these two files to current directory.
INCLUDE=-I../new_kernel/shared -I../new_kernel/linux
DEBUG = 0

ifeq ($(DEBUG), 1)
EXTRA_CFLAGS += -g -D_DEBUG_
endif


SRC = via_utility_server.c  xhklib.c readcfg.c

OBJ = $(SRC:.c=.o)

$(PROGRAM): $(OBJ)
	$(GCC) -m32 -o $(PROGRAM) $(OBJ) $(VIALDFLAGS)

.SUFFIXES: .c

.c.o: .c
	$(GCC) -m32 $(EXTRA_CFLAGS) $(INCLUDE) -fPIC -Wall -c $<

clean:
	rm -f *.o $(PROGRAM)
