{

                // --- check if a dubins path is possible by moving the robot backwards ---//
                // move robot backwards to a reasonable distance
                int maxReverse = 10;
                boolean altPathFound = false;
                // reverse -> check any clash -> no clash -> test dubins -> check clash
                for (int reverse = 1; reverse < maxReverse + 1; reverse++) {
                    double testX = sx, testY = sy;
                    switch (((Number) visitingOrder[step][2]).intValue()) {
                        case 0:
                            testX = testX - reverse;
                            break;
                        case 90:
                            testY = testY - reverse;
                            break;
                        case 180:
                            testX = testX + reverse;
                            break;
                        case 270:
                            testY = testY + reverse;
                            break;
                        default:
                    }
                    // there is no entity clash during reversing
                    if (!arena.entityClash(new int[] { (int) testX, (int) testY })) {
                        DubinsPath testDPObject = DubinsPathDriver.bestDPObject(testX, testY, syaw, ex, ey, eyaw,
                                turning_radius);
                        Object[][] testDP = DubinsPathDriver.bestDPPath(testX, testY, syaw, ex, ey, eyaw,
                                turning_radius);

                        // check if there exists dubins path is valid after reversing
                        gotClash = false;
                        for (Object[] dubinStep : testDP) {
                            if (arena.entityClash(
                                    new int[] { ((Number) dubinStep[0]).intValue(),
                                            ((Number) dubinStep[1]).intValue() })) {
                                gotClash = true;
                                break;
                            }
                        }
                        // if there is a clash...
                        if (gotClash) {
                            // attempt manhattan distance
                            // robot to follow manhattan distance
                            Manhattan manhattan = new Manhattan();
                            Object[][] mPath = manhattan.manhattan(arena, (int) testX, (int) testY, (int) ex, (int) ey);
                            double curYaw = syaw;
                            for (int mStep = 0; mStep < mPath.length - 1; mStep++) {
                                double subSX = ((Number) mPath[mStep][0]).doubleValue();
                                double subSY = ((Number) mPath[mStep][1]).doubleValue();
                                double subEX = ((Number) mPath[mStep + 1][0]).doubleValue();
                                double subEY = ((Number) mPath[mStep + 1][1]).doubleValue();
                                String action = turn(subSX, subSY, curYaw, subEX, subEY);
                                double value = action == "S" ? 1 : 90;
                                robotInstructions.add(String.format("%s-%s", action, value));
                            }
                            altPathFound = true;

                        } else {
                            // if there is no clash of the dubins path
                            for (Object[] dubinStep : testDP) {
                                Object[] toAdd = new Object[] { dubinStep[0], dubinStep[1], dubinStep[2], " " };
                                verbosePath.add(toAdd);
                            }
                            altPathFound = true;
                            System.out.println("Found");
                            // adding the reverse action
                            String rev = "Reverse";
                            double revAmt = reverse;
                            robotInstructions.add(String.format("%s-%s", rev, revAmt));
                            // adding the first segment
                            String s1 = typeToInstr(testDPObject.getPathType())[0];
                            double l1 = arcToAngle(testDPObject.getSegmentLength(0), turning_radius);
                            robotInstructions.add(String.format("%s-%s", s1, l1));
                            // 2nd segment
                            String s2 = typeToInstr(testDPObject.getPathType())[1];
                            double l2 = s2 == "S" ? testDPObject.getSegmentLength(1)
                                    : arcToAngle(testDPObject.getSegmentLength(1), turning_radius);
                            robotInstructions.add(String.format("%s-%s", s2, l2));
                            // 3rd segment
                            String s3 = typeToInstr(testDPObject.getPathType())[2];
                            double l3 = arcToAngle(testDPObject.getSegmentLength(2), turning_radius);
                            robotInstructions.add(String.format("%s-%s", s3, l3));
                        }

                    }
                    // here we go again with the conditional nesting
                    // if an altpath has been found, great! we can continue the traversring to the
                    // next node
                    if (altPathFound) {
                        break;
                    }

                }