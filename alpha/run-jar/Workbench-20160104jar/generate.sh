#!/bin/bash
export CLASSPATH=./generate/java-lib/xalan.jar:./generate/java-lib/xml-apis.jar:./generated/java-lib/xercesImpl.jar:./generate/java-lib/serializer.jar:./generate/java-lib/transform.jar
java -mx512m -ms256m Transform ./xsl/generate_messages.xsl ./definitions/alpha-messages.xml ../../java/com/ev112/codeblack/common/generated/messages/
java -mx512m -ms256m Transform ./xsl/generate_message_dictionary.xsl ./definitions/alpha-message-files.xml ../../java/com/ev112/codeblack/common/generated/messages/
#
#
read -p "<return>"

