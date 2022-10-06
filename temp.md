```java
for (int step = 0; step < robotInstructions.size(); step++) {
            // Determining instruction and extracting values
            String step1 = ((String) robotInstructions.get(step)).substring(1,
                    ((String) robotInstructions.get(step)).length() - 1);

            String step2 = "", step3 = "";
            if (step + 1 < robotInstructions.size()) {
                step2 = ((String) robotInstructions.get(step + 1)).substring(1,
                        ((String) robotInstructions.get(step + 1)).length() - 1);
            }
            if (step + 2 < robotInstructions.size()) {
                step3 = ((String) robotInstructions.get(step + 2)).substring(1,
                        ((String) robotInstructions.get(step + 2)).length() - 1);
            }
            /////
            // if the current step and the next 2 instructions are a dubins path
            /////
            if (step1.matches("fc.*") & step2.matches("fmf.*") & step3.matches("fc.*")) {
                int turn1 = Integer.parseInt(step1.substring(2));
                int move = Integer.parseInt(step2.substring(3));
                int turn2 = Integer.parseInt(step3.substring(2));
                if (move <= FORWARD_CORRECT) {
                    String[] turnResult1 = otsToTurn(turn1).split("&");
                    String[] turnResult2 = otsToTurn(turn2).split("&");
                    // add the first turn
                    newInst.add(turnResult1[0]);
                    if (turnResult1.length == 2) {
                        newInst.add(turnResult1[1]);
                    }

                    newInst.add("\\fmb20;");

                    // add the second turn
                    newInst.add(turnResult2[0]);
                    if (turnResult2.length == 2) {
                        newInst.add(turnResult2[1]);
                    }
                } else {
                    String[] turnResult1 = otsToTurn(turn1).split("&");
                    String[] turnResult2 = otsToTurn(turn2).split("&");
                    String moveResult = "\\fmf" + (move - FORWARD_CORRECT) + ";";
                    // add the first turn
                    newInst.add(turnResult1[0]);
                    if (turnResult1.length == 2) {
                        newInst.add(turnResult1[1]);
                    }
                    // add the move forward
                    newInst.add(moveResult);

                    newInst.add("\\fmb20;");

                    // add the second turn
                    newInst.add(turnResult2[0]);
                    if (turnResult2.length == 2) {
                        newInst.add(turnResult2[1]);
                    }
                }
                if (step + 3 < robotInstructions.size()) {
                    step += 2;
                }
            }
            /////
            // if the current step is image capture
            /////
            else if (step1.matches("AP.*")) {
                step1 = ((String) robotInstructions.get(step)).substring(1);
                newInst.add("C" + step1);
                newInst.add("\\fmb" + REVERSEAMT + ";");
            }
            /////
            // if the current step is move forward and then turn
            /////
            else if (step1.matches("fmf.*") & step2.matches("fc.*")) {
                int move = Integer.parseInt(step1.substring(3));
                int turn = Integer.parseInt(step2.substring(2));
                if (move <= FORWARD_CORRECT) {
                    String[] turnResult = otsToTurn(turn).split("&");
                    // add the turn
                    newInst.add(turnResult[0]);
                    if (turnResult.length == 2) {
                        newInst.add(turnResult[1]);
                    }
                } else {
                    String[] turnResult = otsToTurn(turn).split("&");
                    String moveResult = "\\fmf" + (move - FORWARD_CORRECT) + ";";
                    // add the move
                    newInst.add(moveResult);
                    // add the turn
                    newInst.add(turnResult[0]);
                    if (turnResult.length == 2) {
                        newInst.add(turnResult[1]);
                    }

                }
                if (step + 2 < robotInstructions.size()) {
                    step += 1;
                }
            }
            /////
            // if the current step is turn and then move forward
            /////
            else if (step2.matches("fmf.*") & step1.matches("fc.*")) {
                int move = Integer.parseInt(step2.substring(3));
                int turn = Integer.parseInt(step1.substring(2));
                if (move <= FORWARD_CORRECT) {
                    String[] turnResult = otsToTurn(turn).split("&");

                    // add the turn
                    newInst.add(turnResult[0]);
                    if (turnResult.length == 2) {
                        newInst.add(turnResult[1]);
                    }
                } else {
                    String[] turnResult = otsToTurn(turn).split("&");
                    String moveResult = "\\fmf" + (move - FORWARD_CORRECT) + ";";

                    // add the turn
                    newInst.add(turnResult[0]);
                    if (turnResult.length == 2) {
                        newInst.add(turnResult[1]);
                    }
                    // add the move
                    newInst.add(moveResult);

                }
                if (step + 2 < robotInstructions.size()) {
                    step += 1;
                }

            }
            /////
            // if the current step is only move forward or only turn
            /////
            else if (step1.matches("fmf.*") || step1.matches("fc.*")) {
                newInst.add("\\" + step1 + ";");
            }
        }
````