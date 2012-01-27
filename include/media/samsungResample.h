#ifdef SLSI_S5P6442

#ifndef	SamsungResample_H
#define SamsungResample_H


/******************************************************************************
 *  Resample Defines
 *****************************************************************************/
#define FRAME_160		1
#define FRAME_320		2
#define FRAME_480		3
#define FRAME_640		4
#define FRAME_800		5
#define FRAME_960		6
#define FRAME_1120		7
#define FRAME_1280		8
#define FRAME_1440		9
#define FRAME_1600		10
#define FRAME_1760		11
#define FRAME_1920		12
#define FRAME_2080		13
#define FRAME_2240		14
#define FRAME_2400		15
#define FRAME_2560		16
#define FRAME_2720		17
#define FRAME_2880		18
#define FRAME_3040		19
#define FRAME_3200		20




/******************************************************************************
 *  FUNCTION PROTOTYPES
 *****************************************************************************/

extern "C" {
extern int SamsungDOWNsampleInit(void);
}
extern "C" {
extern int SamsungDOWNsampleInOutConfig(short *Speech, short *OutSignal);
}
extern "C" {
extern int SamsungDOWNsampleFrameSizeConfig(short size);
}
extern "C" {
extern int SamsungDOWNsampleExe(void);
}

#endif		// SamsungResample_H

#endif /* SLSI_S5P6442 */