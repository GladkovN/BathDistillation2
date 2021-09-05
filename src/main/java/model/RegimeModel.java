package model;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.DormandPrince853Integrator;
import org.apache.commons.math3.ode.sampling.FixedStepHandler;
import org.apache.commons.math3.ode.sampling.StepHandler;
import org.apache.commons.math3.ode.sampling.StepNormalizer;

import java.util.ArrayList;
import java.util.Arrays;

public class RegimeModel {

    private final RegimeEquations equations;
    private final double endTime;
    private final ArrayList <Double> time;
    private final ArrayList <Double> potConcentration;
    private final ArrayList <Double> condenserConcentration;
    private final ArrayList <Double> factor;

    public RegimeModel(RegimeEquations equations, double endTime){
        this.equations = equations;
        this.endTime = endTime;
        time = new ArrayList <>();
        potConcentration = new ArrayList <> ();
        condenserConcentration = new ArrayList <>();
        factor = new ArrayList<>();
        this.solve();
    }

    private void solve(){
        FirstOrderIntegrator integrator = new DormandPrince853Integrator(0.000000001, 0.1, 0.00000001, 0.00000001);

        double[] y = this.setInitialCondition();

        StepHandler stepHandler = new StepNormalizer(0.1,
                new FixedStepHandler() {
                    @Override
                    public void init(double t0, double[] y0, double t) {}

                    @Override
                    public void handleStep(double t, double[] y, double[] yDot, boolean isLast) {
                        time.add(t);
                        potConcentration.add(y[0]);
                        condenserConcentration.add(y[equations.getDimension() - 1]);
                        if (equations.getAlpha() < 1) {
                            factor.add(y[0] / y[equations.getDimension() - 1]);
                        } else {
                            factor.add(y[equations.getDimension() - 1] / y[0]);
                        }
                    }
                });
        integrator.addStepHandler(stepHandler);
        integrator.integrate(equations,0.0, y, endTime, y);
    }

    private double[] setInitialCondition(){
        double[] initialCondition = new double[equations.getDimension()];
        double xInput = equations.getInputConcentration();
        Arrays.fill(initialCondition,xInput);
        return initialCondition;
    }

    public ArrayList<Double> getTime () {return time;}

    public ArrayList<Double> getPotConcentration () {return potConcentration;}

    public ArrayList<Double> getCondenserConcentration () {return condenserConcentration;}

    public ArrayList<Double> getFactor(){return factor;}

}
