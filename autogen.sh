#!/bin/sh
LIBTOOLIZE="libtoolize --force --copy --automake"
ACLOCAL="aclocal"
AUTOHEADER="autoheader"
AUTOMAKE="automake -a -c"
AUTOCONF="autoconf"


touch AUTHORS ChangeLog NEWS README

# Discover what version of autoconf we are using.
autoconfversion=`$AUTOCONF --version | head -n 1`
automakeversion=`$AUTOMAKE --version | head -n 1`
libtoolversion=`$LIBTOOLIZE --version | head -n 1`


echo "Using $autoconfversion"
echo "Using $automakeversion"
echo "Using $libtoolversion"
case $autoconfversion in
    *2.5[2-79])
	;;
    *)
	echo "This autoconf version is not supported by NeXus."
	echo "NeXus only supports autoconf 2.5[2-79]."
	echo "You may download it from ftp://ftp.gnu.org/gnu/autoconf"
	exit
	;;
esac

case $automakeversion in
    *1.[6-8]*)
	;;
    *)
	echo "This automake version is not supported by NeXus."
	echo "NeXus only supports automake 1.[6-8].*."
	echo "You may download it from ftp://ftp.gnu.org/gnu/automake"
	exit
	;;
esac

case $libtoolversion in
    *1.5*)
	;;
    *)
	echo "This libtool version is not supported by NeXus."
	echo "NeXus only supports libtool 1.5.*."
	echo "You may download it from ftp://ftp.gnu.org/gnu/libtool"
	exit
	;;
esac

echo -n "Locating GNU m4... "
GNUM4=
for prog in $M4 gm4 gnum4 m4; do
	# continue if $prog generates error (e.g. does not exist)
	( $prog --version ) < /dev/null > /dev/null 2>&1
	if test $? -ne 0 ; then continue; fi

	# /dev/null input prevents a hang of the script for some m4 compilers (e.g. on FreeBSD)
	case `$prog --version < /dev/null 2>&1` in
	*GNU*)	GNUM4=$prog
		break ;;
	esac
done
if test x$GNUM4 = x ; then
	echo "not found."
	exit
else
	echo `which $GNUM4`
fi

# 
if { ! test -d config ; } ; then 
	mkdir config ; 
else 
	rm -rf config/* ;  
fi

# Prepare the use of libtool
if ( $LIBTOOLIZE --version ) < /dev/null > /dev/null 2>&1 ; then
	echo "Preparing the use of libtool ..."
	$LIBTOOLIZE
	if { test -r ltmain.sh ; } ; then mv ltmain.sh config/ ; fi 
	echo "done."
else
	echo "libtoolize not found -- aborting"
	exit
fi

# Generate the Makefiles and configure files
if ( $ACLOCAL --version ) < /dev/null > /dev/null 2>&1; then
	echo "Building macros..."
	$ACLOCAL
	echo "done."
else
	echo "aclocal not found -- aborting"
	exit
fi

if ( $AUTOHEADER --version ) < /dev/null > /dev/null 2>&1; then
	echo "Building config header template..."
	$AUTOHEADER 
	echo "done."
else
	echo "autoheader not found -- aborting"
	exit
fi

if ( $AUTOMAKE --version ) < /dev/null > /dev/null 2>&1; then
	echo "Building Makefile templates..."
	$AUTOMAKE
	echo "done."
else
	echo "automake not found -- aborting"
	exit
fi

if ( $AUTOCONF --version ) < /dev/null > /dev/null 2>&1; then
	echo "Building configure..."
	$AUTOCONF
	echo "done."
else
	echo "autoconf not found -- aborting"
	exit
fi
