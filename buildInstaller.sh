#!/usr/bin/env bash

# This script calls the gradle task with the appropriate prompts for necessary passwords.

# The -s flag to read can be used to shadow the password

echo "Passwords entered below must not contain whitespace/special characters. See"
echo "bash read builtin for details."
echo ""

read -s -p "macKeystorePassword: " macKeystorePassword
echo ""

./gradlew buildInstaller --console=plain -PmacKeystorePassword=$macKeystorePassword
