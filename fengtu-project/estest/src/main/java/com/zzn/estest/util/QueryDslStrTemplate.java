package com.zzn.estest.util;

public class QueryDslStrTemplate {

    public final static String DEVICE_WARN_BY_CONSTANT="{ " +
            "  \"from\": 0, " +
            "  \"size\": 10, " +
            "  \"query\": { " +
            "    \"constant_score\": { " +
            "      \"filter\": { " +
            "        \"bool\": { " +
            "          \"must\": [ " +
            "            { " +
            "              \"match\": { " +
            "                \"carplate\": { " +
            "                  \"query\": \"苏\" " +
            "                } " +
            "              } " +
            "            }, " +
            "            { " +
            "              \"term\": { " +
            "                \"alarmType\": { " +
            "                  \"value\": \"adas\" " +
            "                } " +
            "              } " +
            "            }, " +
            "            { " +
            "              \"term\": { " +
            "                \"alarmSubType\": { " +
            "                  \"value\": \"2\" " +
            "                } " +
            "              } " +
            "            }, " +
            "            { " +
            "              \"term\": { " +
            "                \"alarmLevel\": { " +
            "                  \"value\": \"1\" " +
            "                } " +
            "              } " +
            "            }, " +
            "            { " +
            "              \"range\": { " +
            "                \"alarmTime\": { " +
            "                  \"from\": \"2021-01-08 14:19:34\", " +
            "                  \"to\": \"2021-01-09 14:19:34\" " +
            "                } " +
            "              } " +
            "            }, " +
            "            { " +
            "              \"terms\": { " +
            "                \"taskStatus\": [ " +
            "                  \"2\", " +
            "                  \"3\" " +
            "                ] " +
            "              } " +
            "            }, " +
            "            { " +
            "              \"term\": { " +
            "                \"tenantId\": { " +
            "                  \"value\": \"YZFK\" " +
            "                } " +
            "              } " +
            "            } " +
            "          ], " +
            "          \"must_not\": [ " +
            "            { " +
            "              \"term\": { " +
            "                \"auditResult\": { " +
            "                  \"value\": 2 " +
            "                } " +
            "              } " +
            "            } " +
            "          ] " +
            "        } " +
            "      } " +
            "    } " +
            "  }, " +
            "  \"sort\": [ " +
            "    { " +
            "      \"alarmTime\": { " +
            "        \"order\": \"desc\" " +
            "      } " +
            "    } " +
            "  ] " +
            "}";

}
