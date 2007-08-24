/*
  This is a module which implements the notion of a dataset. Its is
  designed for the use with scripting languages.
  
  copyright: GPL

  Mark Koennecke, October 2002
*/
#ifndef NXDATASET
#define NXDATASET


#define MAGIC 7776622 
/* 
  64 bit problem. On some platforms a 64 bit int is a long, for instance on an alpha,
  on others it is a long long (gcc on intel). Long long is only valid in C99 standard C. 
  This is less then 50 years old, so there may be older compilers out there which do not do 
  long long. Override the define below  with -DNX64INT= as required. Or hack the defines 
  below to properly identify your platform and compiler. Good luck!
*/
#ifndef int64_t
#ifdef __arch64__
  #define int64_t long
  #define uint64_t unsigned long
#else
  #define int64_t long long
  #define uint64_t unsigned long long
#endif
#endif  


typedef struct {
                   int magic;
                   int rank;
                   int type;
                   int *dim;
                   char *format;
                   union {
		     void *ptr;
		     float *fPtr;
		     double *dPtr;
		     int    *iPtr;
		     short int   *sPtr;
		     char   *cPtr;
                     int64_t *lPtr;
		   } u;
}*pNXDS, NXDS;

/*
  include NeXus type codes if not already defined
*/
#ifndef NX_FLOAT32

#define NX_FLOAT32   5
#define NX_FLOAT64   6
#define NX_INT8     20  
#define NX_UINT8    21
#define NX_INT16    22  
#define NX_UINT16   23
#define NX_INT32    24
#define NX_UINT32   25
#define NX_INT64    26
#define NX_UINT64   27
#define NX_CHAR      4

#define NX_MAXRANK 32

#endif


pNXDS createNXDataset(int rank, int typecode, int dim[]);
pNXDS createTextNXDataset(char *name);

void  dropNXDataset(pNXDS dataset);

int   getNXDatasetRank(pNXDS dataset);
int   getNXDatasetDim(pNXDS dataset, int which);
int   getNXDatasetType(pNXDS dataset);
int   getNXDatasetLength(pNXDS dataset);
int   getNXDatasetByteLength(pNXDS dataset);

double getNXDatasetValue(pNXDS dataset, int pos[]);
double getNXDatasetValueAt(pNXDS dataset, int address);
char  *getNXDatasetText(pNXDS dataset);

int   putNXDatasetValue(pNXDS dataset, int pos[], double value);
int   putNXDatasetValueAt(pNXDS dataset, int address, double value);

#endif
