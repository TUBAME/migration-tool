/* $Id: */

#ifndef __COURSECONTENTSERVERCONST_H__
#define __COURSECONTENTSERVERCONST_H__

// Course Content Server:
#define CCSM1           "CourseContentServer: OK"
#define CCSM2           "CourseContentServer: FAILED"
#define COURSECONTENTSERVER     "CourseContentServer"
#define CC_SERVER_SOURCE        FILE_SERVER_SOURCE
#define CC_CLIENT_SOURCE        FILE_CLIENT_SOURCE
#define CC_WEB_SOURCE           FILE_WEB_SOURCE

// Course Content range 1100-1199
#define CC_DOWNLOAD                     1100


// DB Queries
//#define SELECT_CC_FROM_CLASS_ID         "select * from rct_course_content where cc_class_id='"

//#define SELECT_CC_FROM_FILE_NAME        "select * from rct_course_content where cc_class_id='"

//#define SELECT_CC_LOCATION_FROM_CC_ID   "select cc_location from rct_course_content where cc_id='"

//#define SELECT_CC_FROM_ID               "select * from rct_course_content where cc_id='"

#define SELECT_VISIBLE_CC_FROM_CLASS_ID "SELECT * FROM rct_course_content where cc_visible='t' and cc_class_id='"


#endif // __COURSECONTENTSERVERCONST_H__