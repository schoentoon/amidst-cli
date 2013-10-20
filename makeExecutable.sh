#!/bin/bash
cat <(echo -e "#!/bin/sh\nMYSELF=\`which \"\$0\" 2>/dev/null\`\n[ \$? -gt 0 -a -f \"\$0\" ] && MYSELF=\"./\$0\"\njava=java\nif test -n \"\$JAVA_HOME\"; then\n    java=\"\$JAVA_HOME/bin/java\"\nfi\nexec \"\$java\" \$java_args -jar \$MYSELF \"\$@\"\nexit 1\n") ./target/amidst-1.0-SNAPSHOT-jar-with-dependencies.jar > amidst-cli
chmod +x ./amidst-cli
