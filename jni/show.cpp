#include <jni.h>
#include <boost/array.hpp>
#include <boost/property_tree/ptree.hpp>
#include <boost/property_tree/json_parser.hpp>
#include <iostream>
#include <string>


jstring Jni_StrToJson(JNIEnv *env, const char* str)
{
	boost::property_tree::ptree pt;
	std::stringstream stream(str);
	boost::property_tree::read_json(stream, pt);

	boost::property_tree::ptree dataTree = pt.get_child("data");

	std::string save = "";
	boost::property_tree::ptree::const_iterator end = dataTree.end();
	for (boost::property_tree::ptree::const_iterator it = dataTree.begin(); it != end; ++it)
	{
		if (it->second.find("title") != it->second.not_found())
	    {
			std::string strName = it->second.get<std::string>("title");
	        save += strName + "\n\n";
	    }
	}

	return  env->NewStringUTF(save.c_str());
}


extern "C"
{
    JNIEXPORT jstring JNICALL Java_com_example_show_DoubanActivity_StrToJson(JNIEnv *env, jobject obj, jstring str)
    {
    	const char *nativeString = env->GetStringUTFChars(str, 0);

    	return Jni_StrToJson(env, nativeString)/*env->NewStringUTF("sdfsdf")*/;
    }
}



