# Proposed coding styles for WildBee #

## Routing Guides ##

### Prefer object natural identifier to primary key in URL ###

Instead of using '/user/1', we prefer '/user/l.weinan@gmail.com'; instead of using '/task/2', we prefer using '/task/eap6'.

### Using plural form for list; using singular form for show ###

For example, show page should use following URL:

    /package/resteasy

And list page will use plural form:

    /packages

## Coding Guides ##

### Using 'id' instead of 'uuid' for all primary key and foreign key references ###

### All the methods that returning boolean should have suffix 'P' ###

For example:

	def hasNameP(name : String) : Boolean = return true

### If a method work on two tasks, split it into two methods ###

### Prefer immutable object to mutable object whenever possible ###

For example, instead of using 'var':

	def binarySearch(key : Int, a : List[Int]) : (Int, Int) = {
	  var lo : Int = 0
	  var hi : Int = a.length - 1
	  var mid : Int = 0
	  while (lo <= hi) {
		mid = lo + (hi - lo) / 2
		if (key < a(mid)) hi = mid - 1
		else if (key > a(mid)) lo = mid + 1
		else return (key, mid)
	  }
	  (-1, -1)
	}

Try to use 'val' only:

	def binarySearchRecursive(key : Int, a : List[Int], lo : Int, hi : Int) : (Int, Int) = {
	  if (lo <= hi) {
		val mid = lo + (hi - lo) / 2
		if (key < a(mid)) return binarySearchRecursive(key, a, lo, mid-1)
		else if (key > a(mid)) return binarySearchRecursive(key, a, mid+1, hi)
		else return (key, mid)
	  }
	  (-1, -1)
	}

## Table naming guide ##

### Using plural form and underscore for tables ###

For example, user table should be named 'users', and the join table between task and user should be named 'task_users'.




### Coding convention
- Spaces over tabs. We should only use spaces.
- Two spaces for indentation
