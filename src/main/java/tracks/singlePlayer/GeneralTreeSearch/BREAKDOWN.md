# Generic Tree Search

Code written by Wells Lucas Santo with the guidance of Professor Julian Togelius.

This code is conceptualized in two parts:

1. The Tree Search Description Language, and
2. The Generic Tree Search Algorithm

Both components, and the relevant files found in this project, are described below.

# Tree Search Description Language

In order to achieve a generic tree search, a minimal Tree Search Description "Language" was created to describe the various components&ndash;and their values&ndash;that one would expect to find in any tree search algorithm.

## TSDL Parameters

Currently, all tree searches must be described by the following eight parameters:

- Exploration
- Expansion
- Removal
- Simulation
- Evaluation
- Backpropagation
- Selection
- Depth Limit

By specifying values for these eight parameters, you can simulate the execution of familiar tree searches such as Breadth-First Search (BFS), Depth-First Search (DFS), Best First Search / Djikstra's, A*, and the Monte Carlo Tree Search (MCTS). Furtermore, by mixing and matching values, the system is able to support the execution of new, unnamed tree searches, which offers a large potential for procedural generation of new tree search algorithms in the future.

A TSDL file is used to specify the values for each of these eight parameters, to describe what sort of tree search the Generic Tree Search algorithm should perform.

## TSDL File Specification

In order to describe a tree search algorithm, a TSDL file must be placed in the `TSDLAlgorithms` directory that follows the TSDL specification. The file name itself does not affect the execution of the Generic Tree Search Algorithm. Below is an example of a TSDL file (`MCTS.txt`) that describes the MCTS algorithm:

    Exploration: UCT
    Expansion: First
    Removal: No
    Simulation: Random Playout
    Evaluation: Points
    Backpropagation: Increment
    Selection: High
    Depth Limit: 6

Note: All TSDL files *must* specify values for all eight parameters for the Generic Tree Search to operate correctly.
The order with which the parameters are listed does not matter.

In the near future, we will use JSON to describe the TSDL parameters.

## TSDL Parameter Values

Within the `TSDLAlgorithms` directory, the `TSDLParameters.txt` file describes all possible values for each TSDL parameter that has currently been implemented.

**Important:** In order to add any additional values to TSDL, the actual Generic Tree Search Algorithm code must be updated to support the new functionality.

## Value of TSDL

- Reduces the description of *any* tree search algorithm to 8 components
- Allows modular understanding of existing tree search algorithms
- Allows modular development of new tree search algorithms
- Opens up the door for evolution of algorithms by merely changing parameters (mix-and-match)
- Allows for online switching of different tree search components
- Allows for procedural hyperheuristic selection and creation of tree search variants

(To be expanded upon in the full thesis write-up.)

# The Generic Tree Search Algorithm

At the heart of this project is the Generic Tree Search (GTS) algorithm, which has the capability of performing *any* tree search variant using TSDL parameters. The relevant files for the GTS are found within the `/src/controllers/GenericTreeSearch` directory, and are as follows:

- Agent.java
- GTSAlgo.java
- GTSNode.java

The function of each of these three files are described in the following sections.

## Agent.java

This file merely interfaces the GTS with the GVG-AI framework. All controllers in the GVG-AI framework must specify an Agent constructor as well as an `act()` method.

On construction, the Agent will create a new `GTSAlgo` object based on the TSDL file that has been specified. A TSDL file name can be specified either manually in the code, or passed in as a parameter to the constructor.

On `act()`, the Agent will invoke the `eval()` function of GTSAlgo, which returns the action that the current tree search algorithm has selected to perform.

## GTSNode.java

This file describes the nodes that are used to build the tree that is searched by GTS. Each node keeps track of:

- A copy of the game state
- Reward (or Cost) at this state
- Visit count
- The parent node
- All children nodes
- The depth of the node

## GTSAlgo.java

This file is where all of the magic of GTS happens. It is broken up into two main sections:

1. Parameter Initialization and Update
2. Generic Tree Search

### Parameter Initialization and Update

The Parameter Initialization phase includes the constructor, which calls the `initParameters()` method. This method reads from the specified TSDL file in order to initialize the 8 parameters that are used by the generic tree search. Currently, this file reading is done through the use of Java's `BufferedReader` class. In the future, this may be switched out for a JSON-friendly package of some sort.

Currently there is no Parameter Update functionality in GTS. In the future, this functionality may be added in order to support online switching of tree search algorithms within a singular GVG-AI game. Without this piece, the current GTS algorithm will only be able to simulate a single tree search per game, based on the TSDL file it reads on initialization. In the future, an online algorithm evolution piece could be added such that the GTS algorithm can decide whether to load new parameters in based on changing heuristics during gameplay, to simulate the strategy-changing that commonly occurs across the duration of a game.

### Generic Tree Search

The GTS algorithm is split across three methods:

1. `eval()`
2. `explore()`
3. `decide()`

**The `eval()` Method**

This is the method that is directly invoked by `Agent.java` at every step of the game. This method will take 2 parameters: the current state of the game, and the elapsed timer. This method must return the action that GTS decides to perform.

In order to decide what action to perform, GTS must build, or *explore*, the tree of possible states that can be reached from the current, actual state of the game. Nodes in this tree correspond to potential future states that can be reached, and each node maintains a reward (or cost) value that is updated as the algorithm further explores the state tree. The exploration of the tree is done in the `explore()` method.

Before doing the tree exploration, two entities must first be initialized. First, the root node of the state tree must be initialized. This root node represents the current, actual state of the game that the Agent is in, and logically, the state tree will branch out from this root state. More interestingly, GTS also makes use of an ArrayList collection, which is crucial to the execution of several tree search algorithms. The use of this collection is described in the following section.

Finally, this `eval()` method will infinitely loop until either the remaining time in the game is less than 5 milliseconds, or until there are no more possible future states to explore. Once these final conditions are met, the `decide()` method is called in order to return the best action to perform, based on the state tree that was explored by the given tree search algorithm.

**The ArrayList Collection, or Frontier**

In order to perform certain tree search algorithms, the GTS makes use of an ArrayList, which mimics the functionality of various data structures that are employed in almost all tree searches. For instance, while DFS makes use of a stack to determine which node to explore next, BFS uses a queue, and Djikstra's uses a priority queue. In fact, in the generic/generalized description of tree searches by Russell and Norvig, this collection of nodes is described as the **frontier**, which determines which nodes can possibly be explored next by the tree search.

The value of the various TSDL parameters works with the frontier in order to achieve each tree search. For instance, the Exploration parameter determines which node in the frontier collection to visit next. A DFS will select the last node in the frontier to mimic the LIFO operation of a stack. A BFS will select the first node in the frontier to mimic the FIFO operation of a queue.

In other words, this collection is central to the operation of the GTS, *both in practice and in theory*. The frontier is the essential component of GTS that is flexibly used in order to perform various different tree searches all within the same foundational organization.

**The `explore()` Method**

This method explores the state tree using the specified tree search algorithm, updating the reward (or cost) at each node such that the `decide()` method can best select which action to perform.

[ to be added ]

**The `decide()` Method**

This method will use the Selection parameter to make a final decision on what action to perform at this step in the game. This method looks at the children nodes of the root node, and based on the reward (or cost) associated with each node, it will pick the action that corresponds to the node that best fits the Selection parameter.

For example, if the Selection parameter is set to "High", this method will look at all of the children nodes and select the node with the highest reward. It will then return the action that leads from the current state to the state represented by that node.

## Value of GTS

- Verification that tree searches can be described successfully by a generic framework
- Run different tree searches merely by specifying different TSDL parameters
- Verify the possibility of generic algorithms
- Easily extensible to support future tree search algorithms
- Easily evolvable to create new tree search algorithms based on parameter mix-and-match
- Easy to run different tree searches for different games/different game scenarios
- Modularizes tree searches, both conceptually and in practice

(To be expanded upon in the full thesis write-up.)

# Future Roadmap

Below are future pieces that I would like to add on top of the existing GTS code:

1. Exhaustive Search
2. GTS Evolution
3. GTS Parameter Training
4. Hyperheuristic GTS Selection
5. Hyperheuristic GTS Creation

Since the original goal of this project was to work with hyperheuristics, this future roadmap is designed to step the current GTS project in the direction of hooking into a hyperheuristic framework. The current roadmap is a bottom-up approach to Artificial General Intelligence&ndash;by creating a General Tree Search, I have created a general algorithm that is partitioned easily for online algorithm selection and online mix-and-match algorithm creation.

The next step would be to take advantage of the power of TSDL in order to generate many various tree searches and iterate through them to see which ones work best with which games.

Once that's done, the next step is to develop an evolutionary algorithm that is able to evolve TSDL parameters so that the system can autonomously change parameter values to best fit a specific game.

Once the evolution is in place, another component to develop would be some machine learning on the TSDL parameters, so that the system can learn which parameter combinations work best with which games. This would in essence build off of the evolution by remembering the most evolved phenotypes per game, and allow for furthur evolution on future executions of the system.

With all of those components in place, using hyperheuristics is very natural. The use of TSDL with GTS already allows for easy switching between different tree search algorithms. Furthermore, the partitioning of different tree search components allows for the procedural creation/development of new algorithms over time. Having evolution and learning in place essentially completes the hyperheuristic selection/creation process, where tree searches are the fundamental algorithms that the hyperheuristic is able to switch between.
