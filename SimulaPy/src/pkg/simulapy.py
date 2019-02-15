# To change this license header, choose License Headers in Project Properties.
# To change this template file, choose Tools | Templates
# and open the template in the editor.
import sympy as sp

def vrblw(u):
    return "w%s%s" % (u[0], u[1])
    
def vrblu(u):
    return "u%s%s" % (u[0], u[1])
    
def equatns(u):
    return "(u%s%s * w%s%s * %s + u%s%s * w%s%s * %s - O) ** 2" % (u[0], u[1], u[0], u[1], u[0], u[0], u[1], u[1], u[0], u[1] )

if __name__ == "__main__":
    A, B, C, D, E, F, G, H, \
    a, b, c, d, e, f, g, h = sp.symbols('A, B, C, D, E, F, G, H, \
    a, b, c, d, e, f, g, h')
    SAh = ['A', 'B', 'a', 'b']
    SA = ['A','B']
    Sa = ['a','b']
    vars = [vrblw([x,y]) for x in SA for y in Sa ] + [vrblw([y,x]) for x in SA for y in Sa ]\
        +  [vrblu([x,y]) for x in SA  for y in Sa ]   
    print vars
    equations = [equatns([x,y]) for x in SA for y in Sa ]
    print equations
    print reduce(lambda u, v : u + "+" + v, equations)