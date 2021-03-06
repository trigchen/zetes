APPLICATION_NAME = ZetesFeet

ZETES_PATH = ..

include ../common-scripts/globals.mk

SRC = src
BIN = bin
OBJ = obj
LIB = lib

DEBUG_OPTIMIZE = -O0 -g

ifeq ($(UNAME), Darwin)	# OS X
  JAVA_HOME = $(shell /usr/libexec/java_home)
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/darwin"
else ifeq ($(UNAME), Linux)				# linux
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/linux"
else ifeq ($(OS) $(ARCH), Windows_NT i686)		# Windows 32
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"
else ifeq ($(OS) $(ARCH), Windows_NT x86_64)	# Windows 64
  PLATFORM_GENERAL_INCLUDES = -I"$(JAVA_HOME)/include" -I"$(JAVA_HOME)/include/win32"
endif

# Java platform agnostic
JAVA_SOURCE_PATH = $(SRC)/java
JAVA_FILES = $(shell cd $(JAVA_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1')
JAVA_CLASSES = $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_FILES))))

# Java platform specific
JAVA_PLATFORM_SPECIFIC_SOURCE_PATH = $(SRC)/$(PLATFORM_TAG)/java
JAVA_PLATFORM_SPECIFIC_FILES = $(shell if [ -d "$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" ]; then cd $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH); find . -type f -name \*.java | awk '{ sub(/.\//,"") }; 1'; fi)
JAVA_PLATFORM_SPECIFIC_CLASSES = $(addprefix $(JAVA_CLASSPATH)/,$(addsuffix .class,$(basename $(JAVA_PLATFORM_SPECIFIC_FILES))))

# C++ Platform agnostic
CPP_SOURCE_PATH = $(SRC)/cpp
CPP_FILES = $(shell cd $(CPP_SOURCE_PATH); find . -type f -name \*.cpp | awk '{ sub(/.\//,"") }; 1')
CPP_HEADER_FILES = $(addprefix $(CPP_SOURCE_PATH)/,$(shell cd $(CPP_SOURCE_PATH); find . -type f -name \*.h | awk '{ sub(/.\//,"") }; 1'))
CPP_OBJECTS = $(addprefix $(OBJECTS_PATH)/,$(addsuffix .o,$(basename $(CPP_FILES))))

# C static libraries
LIBAVIAN = $(CURDIR)/../avian-pack/avian/build/$(AVIAN_PLATFORM_TAG)/libavian.a

ifeq ($(CLASSPATH), android)
  LIBCRYPTO = $(CURDIR)/../avian-pack/android/openssl-upstream/libcrypto.a
  LIBSSL = $(CURDIR)/../avian-pack/android/openssl-upstream/libssl.a
  LIBEXPAT = $(CURDIR)/../avian-pack/android/external/expat/.libs/libexpat.a
  LIBFDM = $(CURDIR)/../avian-pack/android/external/fdlibm/libfdm.a

  ifeq ($(OS), Windows_NT)
    LIBICUIN = $(CURDIR)/../avian-pack/android/external/icu4c/lib/libsicuin.a
    LIBICUUC = $(CURDIR)/../avian-pack/android/external/icu4c/lib/libsicuuc.a
    LIBICUDATA = $(CURDIR)/../avian-pack/android/external/icu4c/lib/sicudt.a
  else
    LIBICUIN = $(CURDIR)/../avian-pack/android/external/icu4c/lib/libicui18n.a
    LIBICUUC = $(CURDIR)/../avian-pack/android/external/icu4c/lib/libicuuc.a
    LIBICUDATA = $(CURDIR)/../avian-pack/android/external/icu4c/lib/libicudata.a
  endif
else
  LIBCRYPTO = 
  LIBSSL = 
  LIBEXPAT = 
  LIBFDM = 
  LIBICUIN = 
  LIBICUUC = 
  LIBICUDATA = 
endif

# Avian classpath
CLASSPATHJAR = $(CURDIR)/../avian-pack/avian/build/$(AVIAN_PLATFORM_TAG)/classpath.jar

# Target paths
LIBRARY_PATH = $(TARGET)/$(LIB)/$(PLATFORM_TAG)
OBJECTS_PATH = $(TARGET)/$(OBJ)/$(PLATFORM_TAG)
JAVA_LIBRARY_PATH = $(TARGET)/$(LIB)/java
JAVA_OBJECTS_PATH = $(TARGET)/$(OBJ)/java
JAVA_CLASSPATH = $(JAVA_OBJECTS_PATH)/classes

# Target names
LIBRARY_NAME = libzetesfeet.a
JAVA_LIBRARY_NAME = zetesfeet.jar
TOOLS_PATH = tools/$(PLATFORM_TAG)
BIN_TO_OBJ = $(TOOLS_PATH)/binaryToObject$(EXE_EXT)
PACKAGE_NAME = zetesfeet.zip

help:
	@echo "*** This is Zetes build utility: This part builds feet of Zetes ***"
	@echo
	@echo 1. Set up your build environment
	@echo Zetes library needs Java 7 compiler, so JAVA_HOME is to be set correctly. 
	@echo
	@echo Current value: JAVA_HOME=$(JAVA_HOME)
	@echo
	@echo To change it you can type:
	@echo
	@echo "  > export JAVA_HOME=<path>"
	@echo
	@echo Make sure that the current value ARCH=$(ARCH) is a correct architecture of the
	@echo target that you are going to build. If it\'s not, type
	@echo
	@echo "  > export ARCH=<arch>"
	@echo
	@echo "where <arch> is one of: i386, x86_64"
	@echo
	@echo 2. Build Zetes or config IDE 
	@echo To build the library right now from the command line, type 
	@echo
	@echo "  > make all"
	@echo
	@echo To setup project files for Eclipse CDT IDE, type
	@echo
	@echo "  > make ideconf-eclipse"
	@echo
	@echo To clean compiled binary files, type 
	@echo
	@echo "  > make clean"
	@echo

# Other files that should just be copied to the target folder 
JUST_COPY_FILES = $(BIN_TO_OBJ) include/FeetStarter.h include/WinLinMacApi.h
include ../common-scripts/just_copy.mk

all: libzetesfeet $(JAVA_LIBRARY_PATH)/$(JAVA_LIBRARY_NAME) $(JUST_COPY_FILES_TARGET)
	@echo "*** $(APPLICATION_NAME) building process completed successfully. ***"
	@echo You can find the result in folders:
	@echo
	@echo "  $(realpath $(LIBRARY_PATH))"
	@echo "  $(realpath $(JAVA_LIBRARY_PATH))"
	@echo

package: $(TARGET)/$(PACKAGE_NAME)

$(JAVA_CLASSPATH)/%.class: $(JAVA_SOURCE_PATH)/%.java
	@echo [$(APPLICATION_NAME)] Compiling $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -encoding utf8 -sourcepath "$(JAVA_SOURCE_PATH)" -classpath "$(JAVA_CLASSPATH)" -d "$(JAVA_CLASSPATH)" $<

$(JAVA_CLASSPATH)/%.class: $(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)/%.java
	@echo [$(APPLICATION_NAME)] Compiling platform specific $<...
	if [ ! -d "$(dir $@)" ]; then mkdir -p "$(dir $@)"; fi
	"$(JAVA_HOME)/bin/javac" -encoding utf8 -sourcepath "$(JAVA_PLATFORM_SPECIFIC_SOURCE_PATH)" -classpath "$(JAVA_CLASSPATH)" -d "$(JAVA_CLASSPATH)" $<

$(OBJECTS_PATH)/%.o: $(SRC)/cpp/%.cpp $(CPP_HEADER_FILES)
	@echo [$(APPLICATION_NAME)] Compiling $<...
	mkdir -p $(OBJECTS_PATH)
	g++ $(DEBUG_OPTIMIZE) $(PIC) -D_JNI_IMPLEMENTATION_ -c -Iinclude $(PLATFORM_GENERAL_INCLUDES) $< -o $@


ifeq ($(CLASSPATH), android)
libzetesfeet: $(LIBRARY_PATH)/$(LIBRARY_NAME) $(LIBRARY_PATH)/liblist.txt $(LIBRARY_PATH)/libcrypto.a $(LIBRARY_PATH)/libexpat.a $(LIBRARY_PATH)/libicui18n.a $(LIBRARY_PATH)/libicuuc.a $(LIBRARY_PATH)/libicudata.a $(LIBRARY_PATH)/libfdm.a $(LIBRARY_PATH)/libssl.a

$(LIBRARY_PATH)/libcrypto.a: $(LIBCRYPTO)
	@echo [$(APPLICATION_NAME)] Copying $@...
	cp -f $^ $@

$(LIBRARY_PATH)/libexpat.a: $(LIBEXPAT)
	@echo [$(APPLICATION_NAME)] Copying $@...
	cp -f $^ $@

$(LIBRARY_PATH)/libicui18n.a: $(LIBICUIN)
	@echo [$(APPLICATION_NAME)] Copying $@...
	cp -f $^ $@

$(LIBRARY_PATH)/libicuuc.a: $(LIBICUUC)
	@echo [$(APPLICATION_NAME)] Copying $@...
	cp -f $^ $@

$(LIBRARY_PATH)/libicudata.a: $(LIBICUDATA)
	@echo [$(APPLICATION_NAME)] Copying $@...
	cp -f $^ $@

$(LIBRARY_PATH)/libfdm.a: $(LIBFDM)
	@echo [$(APPLICATION_NAME)] Copying $@...
	cp -f $^ $@

$(LIBRARY_PATH)/libssl.a: $(LIBSSL)
	@echo [$(APPLICATION_NAME)] Copying $@...
	cp -f $^ $@

$(LIBCRYPTO):
	(cd $(CURDIR)/../avian-pack; make openssl)
	
$(LIBSSL): $(LIBCRYPTO)

$(LIBEXPAT):
	(cd $(CURDIR)/../avian-pack; make expat)

$(LIBICUIN):
	(cd $(CURDIR)/../avian-pack; make icu4c)
	
$(LIBICUUC): $(LIBICUIN)

$(LIBICUDATA): $(LIBICUIN)

$(LIBFDM):
	(cd $(CURDIR)/../avian-pack; make fdlibm)

$(LIBRARY_PATH)/liblist.txt:
	(echo "libexpat.a" >> $(LIBRARY_PATH)/liblist.txt)
	(echo "libicui18n.a" >> $(LIBRARY_PATH)/liblist.txt)
	(echo "libicuuc.a" >> $(LIBRARY_PATH)/liblist.txt)
	(echo "libicudata.a" >> $(LIBRARY_PATH)/liblist.txt)
	(echo "libfdm.a" >> $(LIBRARY_PATH)/liblist.txt)
	(echo "libssl.a" >> $(LIBRARY_PATH)/liblist.txt)
	(echo "libcrypto.a" >> $(LIBRARY_PATH)/liblist.txt)
		
else
libzetesfeet: $(LIBRARY_PATH)/$(LIBRARY_NAME) $(LIBRARY_PATH)/liblist.txt

$(LIBRARY_PATH)/liblist.txt:
	(echo "" > $(LIBRARY_PATH)/liblist.txt)

endif
	
$(LIBRARY_PATH)/$(LIBRARY_NAME): $(CPP_OBJECTS) $(LIBAVIAN) 
	@echo [$(APPLICATION_NAME)] Constructing $@...
	mkdir -p $(LIBRARY_PATH);
	mkdir -p $(OBJECTS_PATH);
	
	# Extracting libavian objects
	( \
	    cd $(OBJECTS_PATH); \
	    mkdir -p libavian; \
	    cd libavian; \
	    ar x $(LIBAVIAN); \
	)

	mkdir -p $(JAVA_LIBRARY_PATH)

	# Making an object file from the java class library
	
	ar rs $@ $(CPP_OBJECTS)
	ar rs $@ $(OBJECTS_PATH)/libavian/*.o


$(CLASSPATHJAR):
	(cd $(CURDIR)/../avian-pack; make avian-classpath)

$(LIBAVIAN): $(LIBCRYPTO) $(LIBEXPAT) $(LIBICUIN) $(LIBICUUC) $(LIBICUDATA) $(LIBFDM)
	(cd $(CURDIR)/../avian-pack; make avian-static-lib)

$(JAVA_LIBRARY_PATH)/$(JAVA_LIBRARY_NAME): $(CLASSPATHJAR) $(JAVA_CLASSES) $(JAVA_PLATFORM_SPECIFIC_CLASSES)
	@echo [$(APPLICATION_NAME)] Constructing $@...
	mkdir -p $(LIBRARY_PATH);

	# Making the java class library
	cp -f $(CLASSPATHJAR) $(JAVA_LIBRARY_PATH)/$(JAVA_LIBRARY_NAME); \
	( \
	    cd $(JAVA_LIBRARY_PATH); \
	    "$(JAVA_HOME)/bin/jar" uf $(JAVA_LIBRARY_NAME) -C $(CURDIR)/$(JAVA_CLASSPATH) .; \
	)

$(TARGET)/$(PACKAGE_NAME): libzetesfeet $(JAVA_LIBRARY_PATH)/$(JAVA_LIBRARY_NAME) $(JUST_COPY_FILES_TARGET)
	@echo [$(APPLICATION_NAME)] Making package file $@...
	( \
	    cd $(TARGET); \
	    zip $(PACKAGE_NAME) $(subst $(TARGET)/,,$(LIBRARY_PATH)/* $(JAVA_LIBRARY_PATH)/$(JAVA_LIBRARY_NAME) $(JUST_COPY_FILES_TARGET)); \
	)

clean:
	@echo [$(APPLICATION_NAME)] Cleaning all...
	rm -rf $(TARGET)

.PHONY: help all package clean
#.SILENT:

include ../common-scripts/ideconf/ideconf.mk
